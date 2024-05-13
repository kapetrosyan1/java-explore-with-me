package ru.practicum.ewm.event.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventIn(List<Event> events);

    Page<Comment> findByAuthor(User author, Pageable pageable);
}
