package com.software.service;

import com.software.model.Priority;
import com.software.model.Tag;
import com.software.model.ToDo;
import com.software.model.User;
import com.software.repository.TagRepository;
import com.software.repository.ToDoRepository;
import com.software.repository.UserRepository;
import com.software.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TagRepository tagRepository;  // TagRepository bağımlılığını ekliyoruz

    @Autowired
    public ToDoService(ToDoRepository toDoRepository, UserService userService, JwtUtil jwtUtil, TagRepository tagRepository) {
        this.toDoRepository = toDoRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tagRepository = tagRepository;  // TagRepository'yi enjekte ediyoruz
    }

    // Create To Do
    public ToDo createToDo(String title, String description, LocalDate startDate, LocalDate expectedEndDate, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // Yeni ToDo nesnesi oluştur ve kaydet
        ToDo newToDo = new ToDo(
                user,  // Kullanıcıyı ekleyin
                null,  // Tags boş (isteğe bağlı)
                title, // Başlık
                description, // Detaylı açıklama
                startDate,  // Başlangıç tarihi
                expectedEndDate, // Bitiş tarihi
                null  // Varsayılan öncelik, varsayılan bir değer belirledik
        );

        // ToDo nesnesini veritabanına kaydet
        toDoRepository.save(newToDo);

        return newToDo;
    }


    // Delete To Do
    public String deleteToDo(Long toDoId, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // ToDo'yu bul ve kullanıcıyla ilişkisini kontrol et
        Optional<ToDo> optionalToDo = toDoRepository.findById(toDoId);

        if (optionalToDo.isEmpty()) {
            throw new RuntimeException("ToDo not found");
        }

        ToDo toDo = optionalToDo.get();

        if (!toDo.getOwner().equals(user)) {
            throw new RuntimeException("You are not authorized to delete this ToDo");
        }

        toDoRepository.delete(toDo);
        return "ToDo deleted successfully.";
    }

    // Update To Do's Priority
    public ToDo updatePriority(Long toDoId, Priority newPriority, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // ToDo'yu bul ve kullanıcıyla ilişkisini kontrol et
        Optional<ToDo> optionalToDo = toDoRepository.findById(toDoId);

        if (optionalToDo.isEmpty()) {
            throw new RuntimeException("ToDo not found");
        }

        ToDo toDo = optionalToDo.get();

        if (!toDo.getOwner().equals(user)) {
            throw new RuntimeException("You are not authorized to update this ToDo");
        }

        // Yeni önceliği güncelle
        toDo.setTagPriority(newPriority);
        toDoRepository.save(toDo);

        return toDo;
    }

    // Revise LocalDate (Starting Date, Expected End Time)
    public ToDo updateDates(Long toDoId, LocalDate newStartDate, LocalDate newExpectedEndDate, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // ToDo'yu bul ve kullanıcıyla ilişkisini kontrol et
        Optional<ToDo> optionalToDo = toDoRepository.findById(toDoId);

        if (optionalToDo.isEmpty()) {
            throw new RuntimeException("ToDo not found");
        }

        ToDo toDo = optionalToDo.get();

        if (!toDo.getOwner().equals(user)) {
            throw new RuntimeException("You are not authorized to update this ToDo");
        }

        // Yeni tarihleri güncelle
        toDo.setStartingDate(newStartDate);
        toDo.setExpectedEndTime(newExpectedEndDate);
        toDoRepository.save(toDo);

        return toDo;
    }

    // Update Title
    public ToDo updateTitle(Long toDoId, String newTitle, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // ToDo'yu bul ve kullanıcıyla ilişkisini kontrol et
        Optional<ToDo> optionalToDo = toDoRepository.findById(toDoId);

        if (optionalToDo.isEmpty()) {
            throw new RuntimeException("ToDo not found");
        }

        ToDo toDo = optionalToDo.get();

        if (!toDo.getOwner().equals(user)) {
            throw new RuntimeException("You are not authorized to update this ToDo");
        }

        // Yeni başlığı güncelle
        toDo.setTodotitle(newTitle);
        toDoRepository.save(toDo);

        return toDo;
    }

    // Update Description
    public ToDo updateDescription(Long toDoId, String newDescription, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // ToDo'yu bul ve kullanıcıyla ilişkisini kontrol et
        Optional<ToDo> optionalToDo = toDoRepository.findById(toDoId);

        if (optionalToDo.isEmpty()) {
            throw new RuntimeException("ToDo not found");
        }

        ToDo toDo = optionalToDo.get();

        if (!toDo.getOwner().equals(user)) {
            throw new RuntimeException("You are not authorized to update this ToDo");
        }

        // Yeni açıklamayı güncelle
        toDo.setTodoDetailedDescription(newDescription);
        toDoRepository.save(toDo);

        return toDo;
    }

    // Add Tag
    public ToDo addTag(Long toDoId, Long tagId, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // ToDo'yu bul ve kullanıcıyla ilişkisini kontrol et
        Optional<ToDo> optionalToDo = toDoRepository.findById(toDoId);

        if (optionalToDo.isEmpty()) {
            throw new RuntimeException("ToDo not found");
        }

        ToDo toDo = optionalToDo.get();

        if (!toDo.getOwner().equals(user)) {
            throw new RuntimeException("You are not authorized to update this ToDo");
        }

        // Tag'i bul ve ToDo'ya ekle
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        if (optionalTag.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }

        Tag tag = optionalTag.get();
        toDo.getTags().add(tag);
        toDoRepository.save(toDo);

        return toDo;
    }

    // Delete Tag
    public ToDo deleteTag(Long toDoId, Long tagId, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // ToDo'yu bul ve kullanıcıyla ilişkisini kontrol et
        Optional<ToDo> optionalToDo = toDoRepository.findById(toDoId);

        if (optionalToDo.isEmpty()) {
            throw new RuntimeException("ToDo not found");
        }

        ToDo toDo = optionalToDo.get();

        if (!toDo.getOwner().equals(user)) {
            throw new RuntimeException("You are not authorized to update this ToDo");
        }

        // Tag'i bul ve ToDo'dan çıkar
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        if (optionalTag.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }

        Tag tag = optionalTag.get();
        toDo.getTags().remove(tag);
        toDoRepository.save(toDo);

        return toDo;
    }
}
