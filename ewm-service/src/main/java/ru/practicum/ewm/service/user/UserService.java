package ru.practicum.ewm.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.save(user);
        log.info("A user with id {}=has been created", createdUser.getId());
        return UserMapper.toUserDto(createdUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting a user with id={}", id);
        userRepository.deleteById(id);
    }

    public List<UserDto> getUsers(Optional<Long[]> ids, PageRequest pageRequest) {
        if (ids.isEmpty()) {
            Page<User> users = userRepository.findAll(pageRequest);
            log.info("Getting a page of a complete list of users containing {} items",
                    users.getTotalElements());
            return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        List<User> users = userRepository.findAllById(Arrays.asList(ids.get()));
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

}
