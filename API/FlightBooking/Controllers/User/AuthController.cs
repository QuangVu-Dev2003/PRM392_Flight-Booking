using FlightBooking.DTOs.User;
using FlightBooking.Services;
using Microsoft.AspNetCore.Mvc;

namespace FlightBooking.Controllers.User
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IUserService _userService;

        public AuthController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost("register")]
        public async Task<ActionResult<UserProfileDto>> Register([FromBody] RegisterUserDto registerDto)
        {
            try
            {
                var user = await _userService.RegisterAsync(registerDto);
                return CreatedAtAction(nameof(GetProfile), new { userId = user.UserId }, user);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPost("login")]
        public async Task<ActionResult<UserProfileDto>> Login([FromBody] LoginDto loginDto)
        {
            try
            {
                var user = await _userService.LoginAsync(loginDto);
                return Ok(user);
            }
            catch (UnauthorizedAccessException ex)
            {
                return Unauthorized(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpGet("profile/{userId}")]
        public async Task<ActionResult<UserProfileDto>> GetProfile(int userId)
        {
            try
            {
                var user = await _userService.GetProfileAsync(userId);
                return Ok(user);
            }
            catch (ArgumentException ex)
            {
                return NotFound(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPut("profile/{userId}")]
        public async Task<ActionResult<UserProfileDto>> UpdateProfile(int userId, [FromBody] UpdateProfileDto updateDto)
        {
            try
            {
                var user = await _userService.UpdateProfileAsync(userId, updateDto);
                return Ok(user);
            }
            catch (ArgumentException ex)
            {
                return NotFound(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPost("change-password/{userId}")]
        public async Task<ActionResult> ChangePassword(int userId, [FromBody] ChangePasswordDto passwordDto)
        {
            try
            {
                var result = await _userService.ChangePasswordAsync(userId, passwordDto);
                if (result)
                    return Ok(new { message = "Password changed successfully" });
                return BadRequest(new { message = "Failed to change password" });
            }
            catch (UnauthorizedAccessException ex)
            {
                return Unauthorized(new { message = ex.Message });
            }
            catch (ArgumentException ex)
            {
                return NotFound(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
    }
}
