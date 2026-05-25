package org.ies.fenix.server.services;

import org.ies.fenix.controller.dto.tag.TagResponseDTO;
import org.ies.fenix.server.models.Tag;
import org.ies.fenix.server.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<TagResponseDTO> getAll() {
        List<Tag> tags = tagRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Tag::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<TagResponseDTO> response = new ArrayList<>();

        for (Tag tag : tags) {
            response.add(toResponseDTO(tag));
        }

        return response;
    }

    public List<String> getNames() {
        return tagRepository.findAll()
                .stream()
                .map(Tag::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
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