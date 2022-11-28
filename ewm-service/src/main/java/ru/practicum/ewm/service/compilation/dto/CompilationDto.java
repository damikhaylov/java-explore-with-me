package ru.practicum.ewm.service.compilation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private Set<EventShortDto> events = new HashSet<>();

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventShortDto {
        private Long id;
        private String title;
        private String annotation;
        private CategoryDto category;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime eventDate;
        private Boolean paid;
        private UserShortDto initiator;
        private long confirmedRequests;
        private long views;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CategoryDto {
            private Long id;
            private String name;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UserShortDto {
            private Long id;
            private String name;
        }
    }
}
