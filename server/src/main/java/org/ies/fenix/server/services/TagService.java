package org.ies.fenix.server.services;

import dto.tag.TagResponseDTO;
import org.ies.fenix.server.models.Tag;
import org.ies.fenix.server.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<TagResponseDTO> getAll() {
        List<Tag> tags = tagRepository.findAll();
        List<TagResponseDTO> response = new ArrayList<>();

        for (Tag tag : tags) {
            response.add(toResponseDTO(tag));
        }

        return response;
    }

    public TagResponseDTO getById(Integer id) {
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null) {
            return null;
        }
        return toResponseDTO(tag);
    }

    private TagResponseDTO toResponseDTO(Tag tag) {
        TagResponseDTO dto = new TagResponseDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        return dto;
    }
}