package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.comments.dto.CommentDtoCreate;
import ru.practicum.ewm.comments.dto.CommentDtoUpdate;

import java.util.List;

public interface CommentService {
    CommentDto addComment(CommentDtoCreate commentDtoCreate, Long eventId, Long userId);
    CommentDto getCommentById(Long commentId);

    void deleteComment(Long commentId, Long userId);

    CommentDto updateComment(Long commentId, Long userId, CommentDtoUpdate commentDtoUpdate);

    List<CommentDto> findAllCommentsForEvent(Long eventId, Pageable page);

    void deleteCommentByAdmin(Long commentId);


}
