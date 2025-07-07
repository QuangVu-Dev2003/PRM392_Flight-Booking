namespace FlightBooking.DTOs
{
    public class CreateBookingDto
    {
        public int UserId { get; set; }
        public int FlightId { get; set; }
        public List<BookingSeatDto> Seats { get; set; } = new List<BookingSeatDto>();
        public string? Notes { get; set; }
    }

    public class BookingSeatDto
    {
        public int SeatId { get; set; }
        public string PassengerName { get; set; }
        public string? PassengerIdNumber { get; set; }
    }

    public class BookingResponseDto
    {
        public int BookingId { get; set; }
        public string BookingReference { get; set; }
        public string BookingStatus { get; set; }
        public decimal TotalAmount { get; set; }
        public string PaymentStatus { get; set; }
        public DateTime BookingDate { get; set; }
        public FlightResponseDto Flight { get; set; }
        public List<BookedSeatDto> Seats { get; set; } = new List<BookedSeatDto>();
    }

    public class BookedSeatDto
    {
        public string SeatNumber { get; set; }
        public string SeatClassName { get; set; }
        public string PassengerName { get; set; }
        public decimal SeatPrice { get; set; }
    }
}
