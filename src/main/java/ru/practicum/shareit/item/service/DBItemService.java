package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingShortForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ItemCommentException;
import ru.practicum.shareit.exceptions.ItemNotBelongsUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDTO;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.mapper.CommentMapper.modelToResponseDTO;
import static ru.practicum.shareit.item.mapper.CommentMapper.modelToResponseDTOs;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service("dbItemService")
public class DBItemService implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Autowired
    public DBItemService(ItemRepository itemRepository,
                         UserRepository userRepository,
                         BookingRepository bookingRepository,
                         CommentRepository commentRepository,
                         ItemRequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {

        Item itemOfInterest = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId)));
        ItemDto itemDto = toItemDto(itemOfInterest);
        Collection<ResponseCommentDTO> comments = modelToResponseDTOs(commentRepository.findItemComments(itemOfInterest));
        itemDto.setComments(comments);
        if (itemOfInterest.getOwner().getId().intValue() == userId.intValue()) {
            itemDto.setLastBooking(bookingRepository.findItemLastBookings(itemOfInterest).stream().findFirst().orElse(null));
            itemDto.setNextBooking(bookingRepository.findItemNextBookings(itemOfInterest).stream().findFirst().orElse(null));
        }

        return itemDto;
    }

    @Override
    public Collection<ItemDto> getUserItems(Integer userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        Collection<Item> userItems = itemRepository.findAllByOwnerId(userId, pageRequest);
        Collection<BookingShortForItem> previousBookings = bookingRepository.findLastBookings(userItems);
        Collection<BookingShortForItem> followingBookings = bookingRepository.findNextBookings(userItems);
        Map<Integer, List<Comment>> commentsMap = commentRepository.findItemComments(userItems)
                .stream()
                .collect(groupingBy(comment -> comment.getItem().getId()));

        return userItems.stream()
                .sorted(comparing(Item::getId))
                .map(item -> {
                    ItemDto itemDto = toItemDto(item);
                    itemDto.setLastBooking(previousBookings.stream()
                            .filter(booking -> item.getId().intValue() == booking.getItemId())
                            .findFirst()
                            .orElse(null));
                    itemDto.setNextBooking(followingBookings.stream()
                            .filter(booking -> item.getId().intValue() == booking.getItemId())
                            .findFirst()
                            .orElse(null));
                    if (commentsMap.size() > 0) {
                        itemDto.setComments(modelToResponseDTOs(commentsMap.get(item.getId())));
                    }
                    return itemDto;
                }).collect(toList());

    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item savedItem = toModel(itemDto, user);
        if (nonNull(itemDto.getRequestId())) {
            savedItem.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с id %d не найден", userId))));
        }
        return toItemDto(itemRepository.save(savedItem));
    }

    @Override
    public ItemDto updateItem(UpdateItemDto dto, Integer userId, Integer itemId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId)));
        if (item.getOwner().getId().intValue() != userId.intValue()) {
            throw new ItemNotBelongsUserException(String.format("Вещь с id %d не принадлежит пользователю с id %d", itemId, userId));
        }
        if (nonNull(dto.getName())) {
            item.setName(dto.getName());
        }
        if (nonNull(dto.getDescription())) {
            item.setDescription(dto.getDescription());
        }
        if (nonNull(dto.getAvailable())) {
            item.setAvailable(dto.getAvailable());
        }
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public Collection<ItemDto> searchItems(String searchText, Integer from, Integer size) {
        if (searchText.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return toItemDtos(itemRepository.searchItems(searchText, pageRequest));
    }

    @Override
    public ResponseCommentDTO createComment(Integer userId, Integer itemId, RequestCommentDTO requestCommentDTO) {
        User commentAuthor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item commentedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId)));
        Collection<Booking> authorBookings = bookingRepository.findAllByBookerId(userId);
        boolean isCommentPossible = authorBookings.stream()
                .anyMatch(booking -> booking.getItem().getId().intValue() == itemId
                        && booking.getStatus() == Status.APPROVED
                        && booking.getStart().isBefore(LocalDateTime.now()));
        if (!isCommentPossible) {
            throw new ItemCommentException("Комментируемая вещь ещё не бронировалась");
        }
        Comment newComment = Comment.builder()
                .text(requestCommentDTO.getText())
                .item(commentedItem)
                .author(commentAuthor)
                .created(LocalDateTime.now())
                .build();

        return modelToResponseDTO(commentRepository.save(newComment));
    }


}
