package ru.practicum.ewm.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.pagination.CustomPageRequest;
import ru.practicum.ewm.service.user.dto.NewUserDto;
import ru.practicum.ewm.service.user.dto.UserDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/admin/users")
public class UserAdminController {
    private final UserAdminService userAdminService;

    @Autowired
    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated NewUserDto userDto) {
        return userAdminService.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userAdminService.deleteUser(userId);
    }

    @GetMapping()
    public List<UserDto> getUsers(@RequestParam Optional<Long[]> ids,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                  @Positive @RequestParam(defaultValue = "10") int size) {
        return userAdminService.getUsers(ids, new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
