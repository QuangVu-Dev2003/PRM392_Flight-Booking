using FlightBooking.DTOs.User;
using FlightBooking.Models;
using Microsoft.EntityFrameworkCore;

namespace FlightBooking.Services
{
    public class UserService : IUserService
    {
        private readonly FlightBookingContext _context;

        public UserService(FlightBookingContext context)
        {
            _context = context;
        }

        public async Task<UserProfileDto> RegisterAsync(RegisterUserDto registerDto)
        {
            // Check if username or email already exists
            var existingUser = await _context.Users
                .FirstOrDefaultAsync(u => u.Username == registerDto.Username || u.Email == registerDto.Email);

            if (existingUser != null)
                throw new InvalidOperationException("Username or email already exists");

            var user = new User
            {
                Username = registerDto.Username,
                Email = registerDto.Email,
                Password = BCrypt.Net.BCrypt.HashPassword(registerDto.Password),
                FullName = registerDto.FullName,
                Phone = registerDto.Phone,
                DateOfBirth = registerDto.DateOfBirth,
                Gender = registerDto.Gender
            };

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            return new UserProfileDto
            {
                UserId = user.UserId,
                Username = user.Username,
                Email = user.Email,
                FullName = user.FullName,
                Phone = user.Phone,
                DateOfBirth = user.DateOfBirth,
                Gender = user.Gender,
                CreatedAt = user.CreatedAt ?? DateTime.Now,
                TotalBookings = 0
            };
        }

        public async Task<UserProfileDto> LoginAsync(LoginDto loginDto)
        {
            var user = await _context.Users
               .Include(u => u.Bookings)
               .FirstOrDefaultAsync(u =>
                   (u.Username == loginDto.UsernameOrEmail || u.Email == loginDto.UsernameOrEmail)
                   && (u.IsActive ?? true));

            if (user == null || !BCrypt.Net.BCrypt.Verify(loginDto.Password, user.Password))
                throw new UnauthorizedAccessException("Invalid username or password");

            return new UserProfileDto
            {
                UserId = user.UserId,
                Username = user.Username,
                Email = user.Email,
                FullName = user.FullName,
                Phone = user.Phone,
                DateOfBirth = user.DateOfBirth,
                Gender = user.Gender,
                CreatedAt = user.CreatedAt ?? DateTime.Now,
                TotalBookings = user.Bookings.Count
            };
        }

        public async Task<UserProfileDto> GetProfileAsync(int userId)
        {
            var user = await _context.Users
                .Include(u => u.Bookings)
                .FirstOrDefaultAsync(u => u.UserId == userId);

            if (user == null)
                throw new ArgumentException("User not found");

            return new UserProfileDto
            {
                UserId = user.UserId,
                Username = user.Username,
                Email = user.Email,
                FullName = user.FullName,
                Phone = user.Phone,
                DateOfBirth = user.DateOfBirth,
                Gender = user.Gender,
                CreatedAt = user.CreatedAt ?? DateTime.Now,
                TotalBookings = user.Bookings.Count
            };
        }

        public async Task<UserProfileDto> UpdateProfileAsync(int userId, UpdateProfileDto updateDto)
        {
            var user = await _context.Users.FindAsync(userId);
            if (user == null)
                throw new ArgumentException("User not found");

            if (!string.IsNullOrEmpty(updateDto.FullName))
                user.FullName = updateDto.FullName;
            if (updateDto.Phone != null)
                user.Phone = updateDto.Phone;
            if (updateDto.DateOfBirth.HasValue)
                user.DateOfBirth = updateDto.DateOfBirth;
            if (!string.IsNullOrEmpty(updateDto.Gender))
                user.Gender = updateDto.Gender;

            user.UpdatedAt = DateTime.Now;

            await _context.SaveChangesAsync();
            return await GetProfileAsync(userId);
        }

        public async Task<bool> ChangePasswordAsync(int userId, ChangePasswordDto passwordDto)
        {
            var user = await _context.Users.FindAsync(userId);
            if (user == null)
                throw new ArgumentException("User not found");

            if (!BCrypt.Net.BCrypt.Verify(passwordDto.CurrentPassword, user.Password))
                throw new UnauthorizedAccessException("Current password is incorrect");

            user.Password = BCrypt.Net.BCrypt.HashPassword(passwordDto.NewPassword);
            user.UpdatedAt = DateTime.Now;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<List<UserBookingHistoryDto>> GetBookingHistoryAsync(int userId, int page = 1, int pageSize = 10)
        {
            var bookings = await _context.Bookings
                .Include(b => b.Flight)
                    .ThenInclude(f => f.Airline)
                .Include(b => b.Flight.DepartureAirport)
                .Include(b => b.Flight.ArrivalAirport)
                .Include(b => b.BookingSeats)
                .Where(b => b.UserId == userId)
                .OrderByDescending(b => b.BookingDate)
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return bookings.Select(b => new UserBookingHistoryDto
            {
                BookingId = b.BookingId,
                BookingReference = b.BookingReference,
                FlightNumber = b.Flight.FlightNumber,
                AirlineName = b.Flight.Airline.AirlineName,
                Route = $"{b.Flight.DepartureAirport.AirportCode} → {b.Flight.ArrivalAirport.AirportCode}",
                DepartureTime = b.Flight.DepartureTime,
                ArrivalTime = b.Flight.ArrivalTime,
                BookingStatus = b.BookingStatus,
                PaymentStatus = b.PaymentStatus,
                TotalAmount = b.TotalAmount,
                BookingDate = b.BookingDate ?? DateTime.Now,
                PassengerCount = b.BookingSeats.Count,
                CanCancel = b.BookingStatus == "CONFIRMED" && b.Flight.DepartureTime > DateTime.Now.AddHours(24)
            }).ToList();
        }

        public async Task<BookingDetailDto> GetBookingDetailAsync(int userId, int bookingId)
        {
            var booking = await _context.Bookings
                .Include(b => b.Flight)
                    .ThenInclude(f => f.Airline)
                .Include(b => b.Flight.DepartureAirport)
                .Include(b => b.Flight.ArrivalAirport)
                .Include(b => b.Flight.AircraftType)
                .Include(b => b.BookingSeats)
                    .ThenInclude(bs => bs.Seat)
                        .ThenInclude(s => s.Class)
                .FirstOrDefaultAsync(b => b.BookingId == bookingId && b.UserId == userId);

            if (booking == null)
                throw new ArgumentException("Booking not found");

            return new BookingDetailDto
            {
                BookingId = booking.BookingId,
                BookingReference = booking.BookingReference,
                BookingStatus = booking.BookingStatus,
                PaymentStatus = booking.PaymentStatus,
                TotalAmount = booking.TotalAmount,
                BookingDate = booking.BookingDate ?? DateTime.Now,
                Notes = booking.Notes,
                Flight = new FlightDetailDto
                {
                    FlightNumber = booking.Flight.FlightNumber,
                    AirlineName = booking.Flight.Airline.AirlineName,
                    AircraftModel = booking.Flight.AircraftType.AircraftModel,
                    DepartureAirport = $"{booking.Flight.DepartureAirport.AirportName} ({booking.Flight.DepartureAirport.AirportCode})",
                    ArrivalAirport = $"{booking.Flight.ArrivalAirport.AirportName} ({booking.Flight.ArrivalAirport.AirportCode})",
                    DepartureTime = booking.Flight.DepartureTime,
                    ArrivalTime = booking.Flight.ArrivalTime,
                    Gate = booking.Flight.Gate
                },
                Passengers = booking.BookingSeats.Select(bs => new PassengerSeatDto
                {
                    SeatNumber = bs.Seat.SeatNumber,
                    SeatClass = bs.Seat.Class.ClassName,
                    PassengerName = bs.PassengerName,
                    SeatPrice = bs.SeatPrice,
                    IsWindow = bs.Seat.IsWindow ?? false,
                    IsAisle = bs.Seat.IsAisle ?? false
                }).ToList()
            };
        }

        public async Task<bool> CancelBookingAsync(int userId, int bookingId)
        {
            var booking = await _context.Bookings
                .Include(b => b.Flight)
                .Include(b => b.BookingSeats)
                    .ThenInclude(bs => bs.Seat)
                .FirstOrDefaultAsync(b => b.BookingId == bookingId && b.UserId == userId);

            if (booking == null)
                throw new ArgumentException("Booking not found");

            if (booking.BookingStatus != "CONFIRMED")
                throw new InvalidOperationException("Only confirmed bookings can be cancelled");

            if (booking.Flight.DepartureTime <= DateTime.Now.AddHours(24))
                throw new InvalidOperationException("Cannot cancel booking less than 24 hours before departure");

            booking.BookingStatus = "CANCELLED";
            booking.PaymentStatus = "REFUNDED";

            // Release seats
            foreach (var bookingSeat in booking.BookingSeats)
            {
                bookingSeat.Seat.IsAvailable = true;
            }

            await _context.SaveChangesAsync();
            return true;
        }
    }
}
