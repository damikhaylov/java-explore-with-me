package ru.practicum.ewm.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.user.dto.NewUserDto;
import ru.practicum.ewm.service.user.dto.UserDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserAdminService {
    private final UserRepository userRepository;

    @Autowired
    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto createUser(NewUserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.save(user);
        log.info("Creating a new user - a user with id={} has been created", createdUser.getId());
        return UserMapper.toUserDto(createdUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("Deleting a user id={} - a user has been deleted", id);
    }

    public List<UserDto> getUsers(Optional<Long[]> ids, PageRequest pageRequest) {
        String operationNameForLogging = "Listing users for admin ";
        if (ids.isEmpty()) {
            Page<User> users = userRepository.findAll(pageRequest);
            log.info("{} - a page of a complete list of {} items has been compiled",
                    operationNameForLogging, users.getTotalElements());
            return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        List<User> users = userRepository.findAllById(Arrays.asList(ids.get()));
        log.info("{} - a complete list of {} items has been compiled",
                operationNameForLogging, users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

}
