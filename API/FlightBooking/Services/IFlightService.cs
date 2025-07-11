using FlightBooking.DTOs;

namespace FlightBooking.Services
{
    public interface IFlightService
    {
        Task<List<FlightResponseDto>> SearchFlightsAsync(FlightSearchDto searchDto);
        Task<SeatMapDto> GetSeatMapAsync(int flightId);
        Task<BookingResponseDto> CreateBookingAsync(CreateBookingDto bookingDto);
        Task<List<BookingResponseDto>> GetUserBookingsAsync(int userId);
        Task<bool> ConfirmPaymentAsync(int paymentId);
    }
}
