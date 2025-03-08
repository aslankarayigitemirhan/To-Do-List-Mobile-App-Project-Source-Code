package com.software.service;

import com.software.model.Tag;
import com.software.model.User;
import com.software.repository.TagRepository;
import com.software.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private JwtUtil jwtUtil; // JWT doğrulama aracı

    @Autowired
    private UserService userService; // UserService ile kullanıcı bilgilerini alacağız

    // Create Tag
    public Tag createTag(String tagName, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // Yeni Tag nesnesi oluştur
        Tag newTag = new Tag(user,tagName); // Tag'ı kullanıcı ile ilişkilendir
        tagRepository.save(newTag); // Tag'ı veritabanına kaydet

        return newTag;
    }

    // Delete Tag
    public String deleteTag(Long tagId, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // Tag'ı bul
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        if (optionalTag.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }

        Tag tag = optionalTag.get();

        // Tag'ı sil
        tagRepository.delete(tag);

        return "Tag deleted successfully.";
    }

    // Update Tag Name
    public Tag updateTagName(Long tagId, String newTagName, String token) {
        // Token doğrulama ve kullanıcı bilgilerini çıkarma
        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username); // UserService'den kullanıcıyı al

        // Tag'ı bul
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        if (optionalTag.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }

        Tag tag = optionalTag.get();

        // Tag adını güncelle
        tag.setTagName(newTagName);
        tagRepository.save(tag); // Güncellenen Tag'ı veritabanına kaydet

        return tag;
    }
}
