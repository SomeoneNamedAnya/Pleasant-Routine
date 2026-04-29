package org.app.note.service;

import jakarta.persistence.criteria.*;
import org.app.note.PersonalNote;
import org.app.note.TagRoomNote;
import org.app.note.dto.ParamDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class PersonalNoteSpecification {

    public static Specification<PersonalNote> filter(ParamDto dto) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (query != null && !isCountQuery(query)) {
                root.fetch("creator", JoinType.INNER);
                root.fetch("tags", JoinType.LEFT);
                query.distinct(true);
            }


            if (dto.getStart() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), dto.getStart())
                );
            }
            if (dto.getEnd() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("createdAt"), dto.getEnd())
                );
            }

            if (dto.getOwner() != null && !dto.getOwner().isEmpty()) {
                predicates.add(
                        root.get("creator").get("id").in(dto.getOwner())
                );
            }

            if (dto.getTags() != null && !dto.getTags().isEmpty()) {
                for (String tag : dto.getTags()) {
                    if (query == null) continue;
                    Subquery<Long> sub = query.subquery(Long.class);
                    Root<TagRoomNote> subRoot = sub.from(TagRoomNote.class);
                    sub.select(subRoot.get("id"));
                    sub.where(
                            cb.equal(subRoot.get("note"), root),
                            cb.equal(subRoot.get("tag"), tag)
                    );
                    predicates.add(cb.exists(sub));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        return Long.class == query.getResultType()
                || long.class == query.getResultType();
    }
}