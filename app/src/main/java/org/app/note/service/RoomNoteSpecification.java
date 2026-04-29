package org.app.note.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.app.note.RoomNote;
import org.app.note.dto.ParamDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoomNoteSpecification {

    public static Specification<RoomNote> filter(
            ParamDto dto,
            Long roomId,
            Boolean isPublic
    ) {
        return (root, query, cb) -> {

            if (query != null) {
                query.distinct(true);
            }


            List<Predicate> predicates = new ArrayList<>();

            if (roomId != null) {
                predicates.add(cb.equal(root.get("roomId"), roomId));
            }

            if (isPublic != null) {
                predicates.add(cb.equal(root.get("isPublic"), isPublic));
            }
            if (dto.getOwner() != null && !dto.getOwner().isEmpty()) {
                predicates.add(
                        root.get("creator").get("id").in(dto.getOwner())
                );
            }

            if (dto.getStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"), dto.getStart()
                ));
            }

            if (dto.getEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"), dto.getEnd()
                ));
            }

            if (dto.getTags() != null && !dto.getTags().isEmpty()) {
                Join<Object, Object> tagsJoin = root.join("tags");
                predicates.add(tagsJoin.get("tag").in(dto.getTags()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}