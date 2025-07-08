using FlightBooking.Models;
using Microsoft.EntityFrameworkCore;

namespace FlightBooking.Services
{
    public class FlightStatusUpdateService : BackgroundService
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<FlightStatusUpdateService> _logger;

        public FlightStatusUpdateService(IServiceProvider serviceProvider, ILogger<FlightStatusUpdateService> logger)
        {
            _serviceProvider = serviceProvider;
            _logger = logger;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    using var scope = _serviceProvider.CreateScope();
                    var context = scope.ServiceProvider.GetRequiredService<FlightBookingContext>();
                    var notificationService = scope.ServiceProvider.GetRequiredService<INotificationService>();

                    // Update completed flights
                    var completedFlights = await context.Flights
                        .Where(f => f.Status == "SCHEDULED" && f.ArrivalTime < DateTime.Now)
                        .ToListAsync(stoppingToken);

                    foreach (var flight in completedFlights)
                    {
                        flight.Status = "COMPLETED";
                        await notificationService.SendFlightUpdateAsync(
                            flight.FlightId,
                            "COMPLETED",
                            $"Flight {flight.FlightNumber} has completed its journey."
                        );
                    }

                    if (completedFlights.Any())
                    {
                        await context.SaveChangesAsync(stoppingToken);
                        _logger.LogInformation($"Updated {completedFlights.Count} flights to COMPLETED status");
                    }

                    // Send flight reminders (24 hours before departure)
                    var reminderTime = DateTime.Now.AddHours(24);
                    var reminderStart = reminderTime.AddMinutes(-30); // 30 minutes before the exact 24-hour mark
                    var reminderEnd = reminderTime.AddMinutes(30);   // 30 minutes after the exact 24-hour mark

                    var upcomingBookings = await context.Bookings
                        .Include(b => b.Flight)
                        .Where(b => b.BookingStatus == "CONFIRMED" &&
                                   b.Flight.DepartureTime >= reminderStart &&
                                   b.Flight.DepartureTime <= reminderEnd)
                        .ToListAsync(stoppingToken);

                    foreach (var booking in upcomingBookings)
                    {
                        await notificationService.SendFlightReminderAsync(booking.BookingId);
                    }

                    if (upcomingBookings.Any())
                    {
                        _logger.LogInformation($"Sent {upcomingBookings.Count} flight reminders");
                    }

                    // Check for delayed flights
                    var delayedFlights = await context.Flights
                        .Where(f => f.Status == "DELAYED")
                        .ToListAsync(stoppingToken);

                    foreach (var flight in delayedFlights)
                    {
                        await notificationService.SendFlightUpdateAsync(
                            flight.FlightId,
                            "DELAYED",
                            $"Flight {flight.FlightNumber} delayed. Please check new time."
                        );
                    }

                    // Check for cancelled flights
                    var cancelledFlights = await context.Flights
                        .Where(f => f.Status == "CANCELLED")
                        .ToListAsync(stoppingToken);

                    foreach (var flight in cancelledFlights)
                    {
                        await notificationService.SendFlightUpdateAsync(
                            flight.FlightId,
                            "CANCELLED",
                            $"Flight {flight.FlightNumber} has been canceled. Please contact us for assistance."
                        );
                    }
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Error occurred while updating flight statuses");
                }

                // Run every hour
                await Task.Delay(TimeSpan.FromHours(1), stoppingToken);
            }
        }
    }
}