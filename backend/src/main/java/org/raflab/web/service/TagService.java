package org.raflab.web.service;

import org.raflab.web.dto.response.TagResponse;
import org.raflab.web.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;

public class TagService {
    private final TagRepository tagRepository = new TagRepository();

    public List<TagResponse> getAll(int page) {
        int limit = 20; // Za tagove možemo više po stranici
        int offset = (page - 1) * limit;
        return tagRepository.findAllPaginated(offset, limit).stream()
                .map(t -> new TagResponse(t.getId(), t.getKljucnaRec()))
                .collect(Collectors.toList());
    }
}