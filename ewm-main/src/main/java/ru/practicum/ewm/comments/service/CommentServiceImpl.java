package ru.practicum.ewm.comments.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentDtoCreate;
import ru.practicum.ewm.comments.dto.CommentDtoUpdate;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    EventRepository eventRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto addComment(CommentDtoCreate commentDtoCreate, Long eventId, Long userId) {
        User user = checkUserExistence(userId);
        Event event = checkEventExistence(eventId);
        Comment comment = CommentMapper.toCommentFromCommentDtoCreate(commentDtoCreate, event, user);
        log.info("Сохранен комментарий с id {}", comment.getId());
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.toCommentDto(checkCommentExistence(commentId));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = checkCommentExistence(commentId);
        checkUserExistence(userId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Нельзя удалить чужой комментарий");
        }
        log.info("Удалён комментарий с id {}", commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, Long userId, CommentDtoUpdate commentDtoUpdate) {
        Comment comment = checkCommentExistence(commentId);
        User user = checkUserExistence(userId);
        if (userId != comment.getAuthor().getId()) {
            throw new ConflictException("Нельзя отредактировать чужой комментарий");
        }

        if (commentDtoUpdate.getText() != null) {
            comment.setText(commentDtoUpdate.getText());
        }
        log.info("Обновлен комментарий с id {}", comment.getId());
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findAllCommentsForEvent(Long eventId, Pageable page) {
        checkEventExistence(eventId);
        log.info("Найден список комментов");
        return commentRepository.findAllByEventId(eventId, page).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = checkCommentExistence(commentId);
        log.info("Админом удалён комментарий с id {}", commentId);
        commentRepository.deleteById(commentId);
    }

    private Comment checkCommentExistence(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException("Не найден комментарий с id " + commentId);
        });
        return comment;
    }

    private Event checkEventExistence(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Не найдено событие с id " + eventId);
        });
        return event;
    }

    private User checkUserExistence(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Не найдено событие с id " + userId);
        });
        return user;
    }
}
