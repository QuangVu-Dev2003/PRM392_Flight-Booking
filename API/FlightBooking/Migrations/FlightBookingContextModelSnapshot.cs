﻿// <auto-generated />
using System;
using FlightBooking.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

#nullable disable

namespace FlightBooking.Migrations
{
    [DbContext(typeof(FlightBookingContext))]
    partial class FlightBookingContextModelSnapshot : ModelSnapshot
    {
        protected override void BuildModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "8.0.10")
                .HasAnnotation("Relational:MaxIdentifierLength", 128);

            SqlServerModelBuilderExtensions.UseIdentityColumns(modelBuilder);

            modelBuilder.Entity("FlightBooking.Models.AircraftType", b =>
                {
                    b.Property<int>("AircraftTypeId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("aircraft_type_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("AircraftTypeId"));

                    b.Property<string>("AircraftModel")
                        .IsRequired()
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)")
                        .HasColumnName("aircraft_model");

                    b.Property<int>("BusinessSeats")
                        .HasColumnType("int")
                        .HasColumnName("business_seats");

                    b.Property<int>("EconomySeats")
                        .HasColumnType("int")
                        .HasColumnName("economy_seats");

                    b.Property<int>("FirstClassSeats")
                        .HasColumnType("int")
                        .HasColumnName("first_class_seats");

                    b.Property<string>("Manufacturer")
                        .IsRequired()
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)")
                        .HasColumnName("manufacturer");

                    b.Property<string>("SeatMapLayout")
                        .IsRequired()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasColumnName("seat_map_layout");

                    b.Property<int>("TotalSeats")
                        .HasColumnType("int")
                        .HasColumnName("total_seats");

                    b.HasKey("AircraftTypeId")
                        .HasName("PK__aircraft__7F5C1091E227CCCB");

                    b.ToTable("aircraft_types", (string)null);
                });

            modelBuilder.Entity("FlightBooking.Models.Airline", b =>
                {
                    b.Property<int>("AirlineId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("airline_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("AirlineId"));

                    b.Property<string>("AirlineCode")
                        .IsRequired()
                        .HasMaxLength(10)
                        .HasColumnType("nvarchar(10)")
                        .HasColumnName("airline_code");

                    b.Property<string>("AirlineName")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)")
                        .HasColumnName("airline_name");

                    b.Property<DateTime?>("CreatedAt")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("created_at")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<string>("LogoUrl")
                        .HasMaxLength(255)
                        .HasColumnType("nvarchar(255)")
                        .HasColumnName("logo_url");

                    b.HasKey("AirlineId")
                        .HasName("PK__airlines__A016BF80A699215B");

                    b.HasIndex(new[] { "AirlineCode" }, "UQ__airlines__7E72435649F5EB1B")
                        .IsUnique();

                    b.ToTable("airlines", (string)null);
                });

            modelBuilder.Entity("FlightBooking.Models.Airport", b =>
                {
                    b.Property<int>("AirportId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("airport_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("AirportId"));

                    b.Property<string>("AirportCode")
                        .IsRequired()
                        .HasMaxLength(10)
                        .HasColumnType("nvarchar(10)")
                        .HasColumnName("airport_code");

                    b.Property<string>("AirportName")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)")
                        .HasColumnName("airport_name");

                    b.Property<string>("City")
                        .IsRequired()
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)")
                        .HasColumnName("city");

                    b.Property<string>("Country")
                        .IsRequired()
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)")
                        .HasColumnName("country");

                    b.Property<DateTime?>("CreatedAt")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("created_at")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<string>("Timezone")
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)")
                        .HasColumnName("timezone");

                    b.HasKey("AirportId")
                        .HasName("PK__airports__C795D516C911C753");

                    b.HasIndex(new[] { "AirportCode" }, "UQ__airports__E949ADC76B684E40")
                        .IsUnique();

                    b.ToTable("airports", (string)null);
                });

            modelBuilder.Entity("FlightBooking.Models.Booking", b =>
                {
                    b.Property<int>("BookingId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("booking_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("BookingId"));

                    b.Property<DateTime?>("BookingDate")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("booking_date")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<string>("BookingReference")
                        .IsRequired()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasColumnName("booking_reference");

                    b.Property<string>("BookingStatus")
                        .ValueGeneratedOnAdd()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasDefaultValue("CONFIRMED")
                        .HasColumnName("booking_status");

                    b.Property<int>("FlightId")
                        .HasColumnType("int")
                        .HasColumnName("flight_id");

                    b.Property<string>("Notes")
                        .HasColumnType("ntext")
                        .HasColumnName("notes");

                    b.Property<string>("PaymentStatus")
                        .ValueGeneratedOnAdd()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasDefaultValue("PENDING")
                        .HasColumnName("payment_status");

                    b.Property<decimal>("TotalAmount")
                        .HasColumnType("decimal(10, 2)")
                        .HasColumnName("total_amount");

                    b.Property<int>("UserId")
                        .HasColumnType("int")
                        .HasColumnName("user_id");

                    b.HasKey("BookingId")
                        .HasName("PK__bookings__5DE3A5B15613F43D");

                    b.HasIndex("FlightId");

                    b.HasIndex(new[] { "BookingReference" }, "IX_bookings_reference");

                    b.HasIndex(new[] { "BookingStatus" }, "IX_bookings_status");

                    b.HasIndex(new[] { "UserId" }, "IX_bookings_user");

                    b.HasIndex(new[] { "BookingReference" }, "UQ__bookings__BADA4559CFA36B8E")
                        .IsUnique();

                    b.ToTable("bookings", (string)null);
                });

            modelBuilder.Entity("FlightBooking.Models.BookingSeat", b =>
                {
                    b.Property<int>("BookingSeatId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("booking_seat_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("BookingSeatId"));

                    b.Property<int>("BookingId")
                        .HasColumnType("int")
                        .HasColumnName("booking_id");

                    b.Property<DateTime?>("CreatedAt")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("created_at")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<string>("PassengerIdNumber")
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)")
                        .HasColumnName("passenger_id_number");

                    b.Property<string>("PassengerName")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)")
                        .HasColumnName("passenger_name");

                    b.Property<int>("SeatId")
                        .HasColumnType("int")
                        .HasColumnName("seat_id");

                    b.Property<decimal>("SeatPrice")
                        .HasColumnType("decimal(10, 2)")
                        .HasColumnName("seat_price");

                    b.HasKey("BookingSeatId")
                        .HasName("PK__booking___C073D47DEA505F8B");

                    b.HasIndex("SeatId");

                    b.HasIndex(new[] { "BookingId", "SeatId" }, "UQ_booking_seats")
                        .IsUnique();

                    b.ToTable("booking_seats", null, t =>
                        {
                            t.HasTrigger("tr_restore_seat_availability");

                            t.HasTrigger("tr_update_seat_availability");
                        });

                    b.HasAnnotation("SqlServer:UseSqlOutputClause", false);
                });

            modelBuilder.Entity("FlightBooking.Models.Flight", b =>
                {
                    b.Property<int>("FlightId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("flight_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("FlightId"));

                    b.Property<int>("AircraftTypeId")
                        .HasColumnType("int")
                        .HasColumnName("aircraft_type_id");

                    b.Property<int>("AirlineId")
                        .HasColumnType("int")
                        .HasColumnName("airline_id");

                    b.Property<int>("ArrivalAirportId")
                        .HasColumnType("int")
                        .HasColumnName("arrival_airport_id");

                    b.Property<DateTime>("ArrivalTime")
                        .HasColumnType("datetime2")
                        .HasColumnName("arrival_time");

                    b.Property<decimal>("BasePrice")
                        .HasColumnType("decimal(10, 2)")
                        .HasColumnName("base_price");

                    b.Property<DateTime?>("CreatedAt")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("created_at")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<int>("DepartureAirportId")
                        .HasColumnType("int")
                        .HasColumnName("departure_airport_id");

                    b.Property<DateTime>("DepartureTime")
                        .HasColumnType("datetime2")
                        .HasColumnName("departure_time");

                    b.Property<string>("FlightNumber")
                        .IsRequired()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasColumnName("flight_number");

                    b.Property<string>("Gate")
                        .HasMaxLength(10)
                        .HasColumnType("nvarchar(10)")
                        .HasColumnName("gate");

                    b.Property<string>("Status")
                        .ValueGeneratedOnAdd()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasDefaultValue("SCHEDULED")
                        .HasColumnName("status");

                    b.HasKey("FlightId")
                        .HasName("PK__flights__E3705765D1845355");

                    b.HasIndex("AircraftTypeId");

                    b.HasIndex("AirlineId");

                    b.HasIndex("ArrivalAirportId");

                    b.HasIndex(new[] { "DepartureAirportId", "ArrivalAirportId", "DepartureTime" }, "IX_flights_route_date");

                    b.HasIndex(new[] { "Status" }, "IX_flights_status");

                    b.ToTable("flights", (string)null);
                });

            modelBuilder.Entity("FlightBooking.Models.Notification", b =>
                {
                    b.Property<int>("NotificationId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("NotificationId"));

                    b.Property<string>("AdditionalData")
                        .HasMaxLength(1000)
                        .HasColumnType("nvarchar(1000)");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("datetime2");

                    b.Property<string>("Message")
                        .IsRequired()
                        .HasMaxLength(500)
                        .HasColumnType("nvarchar(500)");

                    b.Property<DateTime?>("ReadAt")
                        .HasColumnType("datetime2");

                    b.Property<int?>("RelatedBookingId")
                        .HasColumnType("int");

                    b.Property<int?>("RelatedFlightId")
                        .HasColumnType("int");

                    b.Property<string>("Status")
                        .IsRequired()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)");

                    b.Property<string>("Title")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)");

                    b.Property<string>("Type")
                        .IsRequired()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)");

                    b.Property<int>("UserId")
                        .HasColumnType("int");

                    b.HasKey("NotificationId");

                    b.HasIndex("CreatedAt");

                    b.HasIndex("RelatedBookingId");

                    b.HasIndex("RelatedFlightId");

                    b.HasIndex("UserId", "Status");

                    b.ToTable("Notifications");
                });

            modelBuilder.Entity("FlightBooking.Models.PasswordResetToken", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<string>("Email")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)");

                    b.Property<DateTime>("ExpirationTime")
                        .HasColumnType("datetime2");

                    b.Property<bool>("IsUsed")
                        .HasColumnType("bit");

                    b.Property<string>("OtpCode")
                        .IsRequired()
                        .HasMaxLength(6)
                        .HasColumnType("nvarchar(6)");

                    b.HasKey("Id");

                    b.ToTable("PasswordResetTokens");
                });

            modelBuilder.Entity("FlightBooking.Models.Payment", b =>
                {
                    b.Property<int>("PaymentId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("PaymentId"));

                    b.Property<decimal>("Amount")
                        .HasColumnType("decimal(10,2)");

                    b.Property<int>("BookingId")
                        .HasColumnType("int");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("datetime2");

                    b.Property<string>("Notes")
                        .HasMaxLength(500)
                        .HasColumnType("nvarchar(500)");

                    b.Property<string>("PaymentData")
                        .HasMaxLength(1000)
                        .HasColumnType("nvarchar(1000)");

                    b.Property<string>("PaymentMethod")
                        .IsRequired()
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)");

                    b.Property<DateTime?>("ProcessedAt")
                        .HasColumnType("datetime2");

                    b.Property<string>("Status")
                        .IsRequired()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)");

                    b.Property<string>("TransactionId")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)");

                    b.HasKey("PaymentId");

                    b.HasIndex("BookingId");

                    b.HasIndex("TransactionId")
                        .IsUnique();

                    b.ToTable("Payments");
                });

            modelBuilder.Entity("FlightBooking.Models.Seat", b =>
                {
                    b.Property<int>("SeatId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("seat_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("SeatId"));

                    b.Property<int>("ClassId")
                        .HasColumnType("int")
                        .HasColumnName("class_id");

                    b.Property<DateTime?>("CreatedAt")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("created_at")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<decimal?>("ExtraFee")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("decimal(10, 2)")
                        .HasDefaultValue(0m)
                        .HasColumnName("extra_fee");

                    b.Property<int>("FlightId")
                        .HasColumnType("int")
                        .HasColumnName("flight_id");

                    b.Property<bool?>("IsAisle")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("bit")
                        .HasDefaultValue(false)
                        .HasColumnName("is_aisle");

                    b.Property<bool?>("IsAvailable")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("bit")
                        .HasDefaultValue(true)
                        .HasColumnName("is_available");

                    b.Property<bool?>("IsEmergencyExit")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("bit")
                        .HasDefaultValue(false)
                        .HasColumnName("is_emergency_exit");

                    b.Property<bool?>("IsWindow")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("bit")
                        .HasDefaultValue(false)
                        .HasColumnName("is_window");

                    b.Property<string>("SeatColumn")
                        .IsRequired()
                        .HasMaxLength(2)
                        .HasColumnType("nvarchar(2)")
                        .HasColumnName("seat_column");

                    b.Property<string>("SeatNumber")
                        .IsRequired()
                        .HasMaxLength(10)
                        .HasColumnType("nvarchar(10)")
                        .HasColumnName("seat_number");

                    b.Property<int>("SeatRow")
                        .HasColumnType("int")
                        .HasColumnName("seat_row");

                    b.HasKey("SeatId")
                        .HasName("PK__seats__906DED9C4F3271E6");

                    b.HasIndex(new[] { "ClassId" }, "IX_seats_class");

                    b.HasIndex(new[] { "FlightId", "IsAvailable" }, "IX_seats_flight_available");

                    b.HasIndex(new[] { "FlightId", "SeatNumber" }, "UQ_seats_flight_number")
                        .IsUnique();

                    b.ToTable("seats", (string)null);
                });

            modelBuilder.Entity("FlightBooking.Models.SeatClass", b =>
                {
                    b.Property<int>("ClassId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("class_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("ClassId"));

                    b.Property<string>("ClassDescription")
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)")
                        .HasColumnName("class_description");

                    b.Property<string>("ClassName")
                        .IsRequired()
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasColumnName("class_name");

                    b.Property<decimal?>("PriceMultiplier")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("decimal(3, 2)")
                        .HasDefaultValue(1.0m)
                        .HasColumnName("price_multiplier");

                    b.HasKey("ClassId")
                        .HasName("PK__seat_cla__FDF47986A02EAC4B");

                    b.ToTable("seat_classes", (string)null);
                });

            modelBuilder.Entity("FlightBooking.Models.User", b =>
                {
                    b.Property<int>("UserId")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasColumnName("user_id");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("UserId"));

                    b.Property<DateTime?>("CreatedAt")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("created_at")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<DateOnly?>("DateOfBirth")
                        .HasColumnType("date")
                        .HasColumnName("date_of_birth");

                    b.Property<string>("Email")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)")
                        .HasColumnName("email");

                    b.Property<string>("FullName")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("nvarchar(100)")
                        .HasColumnName("full_name");

                    b.Property<string>("Gender")
                        .HasMaxLength(10)
                        .HasColumnType("nvarchar(10)")
                        .HasColumnName("gender");

                    b.Property<bool?>("IsActive")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("bit")
                        .HasDefaultValue(true)
                        .HasColumnName("is_active");

                    b.Property<string>("Password")
                        .IsRequired()
                        .HasMaxLength(255)
                        .HasColumnType("nvarchar(255)")
                        .HasColumnName("password");

                    b.Property<string>("Phone")
                        .HasMaxLength(20)
                        .HasColumnType("nvarchar(20)")
                        .HasColumnName("phone");

                    b.Property<DateTime?>("UpdatedAt")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("datetime2")
                        .HasColumnName("updated_at")
                        .HasDefaultValueSql("(getdate())");

                    b.Property<string>("Username")
                        .IsRequired()
                        .HasMaxLength(50)
                        .HasColumnType("nvarchar(50)")
                        .HasColumnName("username");

                    b.HasKey("UserId")
                        .HasName("PK__users__B9BE370F2366BD93");

                    b.HasIndex(new[] { "Email" }, "IX_users_email");

                    b.HasIndex(new[] { "Username" }, "IX_users_username");

                    b.HasIndex(new[] { "Email" }, "UQ__users__AB6E616475C24318")
                        .IsUnique();

                    b.HasIndex(new[] { "Username" }, "UQ__users__F3DBC57289A37B25")
                        .IsUnique();

                    b.ToTable("users", null, t =>
                        {
                            t.HasTrigger("tr_users_update_timestamp");
                        });

                    b.HasAnnotation("SqlServer:UseSqlOutputClause", false);
                });

            modelBuilder.Entity("FlightBooking.Models.Booking", b =>
                {
                    b.HasOne("FlightBooking.Models.Flight", "Flight")
                        .WithMany("Bookings")
                        .HasForeignKey("FlightId")
                        .IsRequired()
                        .HasConstraintName("FK_bookings_flight");

                    b.HasOne("FlightBooking.Models.User", "User")
                        .WithMany("Bookings")
                        .HasForeignKey("UserId")
                        .IsRequired()
                        .HasConstraintName("FK_bookings_user");

                    b.Navigation("Flight");

                    b.Navigation("User");
                });

            modelBuilder.Entity("FlightBooking.Models.BookingSeat", b =>
                {
                    b.HasOne("FlightBooking.Models.Booking", "Booking")
                        .WithMany("BookingSeats")
                        .HasForeignKey("BookingId")
                        .IsRequired()
                        .HasConstraintName("FK_booking_seats_booking");

                    b.HasOne("FlightBooking.Models.Seat", "Seat")
                        .WithMany("BookingSeats")
                        .HasForeignKey("SeatId")
                        .IsRequired()
                        .HasConstraintName("FK_booking_seats_seat");

                    b.Navigation("Booking");

                    b.Navigation("Seat");
                });

            modelBuilder.Entity("FlightBooking.Models.Flight", b =>
                {
                    b.HasOne("FlightBooking.Models.AircraftType", "AircraftType")
                        .WithMany("Flights")
                        .HasForeignKey("AircraftTypeId")
                        .IsRequired()
                        .HasConstraintName("FK_flights_aircraft");

                    b.HasOne("FlightBooking.Models.Airline", "Airline")
                        .WithMany("Flights")
                        .HasForeignKey("AirlineId")
                        .IsRequired()
                        .HasConstraintName("FK_flights_airline");

                    b.HasOne("FlightBooking.Models.Airport", "ArrivalAirport")
                        .WithMany("FlightArrivalAirports")
                        .HasForeignKey("ArrivalAirportId")
                        .IsRequired()
                        .HasConstraintName("FK_flights_arrival");

                    b.HasOne("FlightBooking.Models.Airport", "DepartureAirport")
                        .WithMany("FlightDepartureAirports")
                        .HasForeignKey("DepartureAirportId")
                        .IsRequired()
                        .HasConstraintName("FK_flights_departure");

                    b.Navigation("AircraftType");

                    b.Navigation("Airline");

                    b.Navigation("ArrivalAirport");

                    b.Navigation("DepartureAirport");
                });

            modelBuilder.Entity("FlightBooking.Models.Notification", b =>
                {
                    b.HasOne("FlightBooking.Models.Booking", "RelatedBooking")
                        .WithMany()
                        .HasForeignKey("RelatedBookingId");

                    b.HasOne("FlightBooking.Models.Flight", "RelatedFlight")
                        .WithMany()
                        .HasForeignKey("RelatedFlightId");

                    b.HasOne("FlightBooking.Models.User", "User")
                        .WithMany()
                        .HasForeignKey("UserId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("RelatedBooking");

                    b.Navigation("RelatedFlight");

                    b.Navigation("User");
                });

            modelBuilder.Entity("FlightBooking.Models.Payment", b =>
                {
                    b.HasOne("FlightBooking.Models.Booking", "Booking")
                        .WithMany()
                        .HasForeignKey("BookingId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Booking");
                });

            modelBuilder.Entity("FlightBooking.Models.Seat", b =>
                {
                    b.HasOne("FlightBooking.Models.SeatClass", "Class")
                        .WithMany("Seats")
                        .HasForeignKey("ClassId")
                        .IsRequired()
                        .HasConstraintName("FK_seats_class");

                    b.HasOne("FlightBooking.Models.Flight", "Flight")
                        .WithMany("Seats")
                        .HasForeignKey("FlightId")
                        .IsRequired()
                        .HasConstraintName("FK_seats_flight");

                    b.Navigation("Class");

                    b.Navigation("Flight");
                });

            modelBuilder.Entity("FlightBooking.Models.AircraftType", b =>
                {
                    b.Navigation("Flights");
                });

            modelBuilder.Entity("FlightBooking.Models.Airline", b =>
                {
                    b.Navigation("Flights");
                });

            modelBuilder.Entity("FlightBooking.Models.Airport", b =>
                {
                    b.Navigation("FlightArrivalAirports");

                    b.Navigation("FlightDepartureAirports");
                });

            modelBuilder.Entity("FlightBooking.Models.Booking", b =>
                {
                    b.Navigation("BookingSeats");
                });

            modelBuilder.Entity("FlightBooking.Models.Flight", b =>
                {
                    b.Navigation("Bookings");

                    b.Navigation("Seats");
                });

            modelBuilder.Entity("FlightBooking.Models.Seat", b =>
                {
                    b.Navigation("BookingSeats");
                });

            modelBuilder.Entity("FlightBooking.Models.SeatClass", b =>
                {
                    b.Navigation("Seats");
                });

            modelBuilder.Entity("FlightBooking.Models.User", b =>
                {
                    b.Navigation("Bookings");
                });
#pragma warning restore 612, 618
        }
    }
}
