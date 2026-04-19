package org.app.links;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.auth.config.LinkDto;
import org.app.user.domain.User;
import org.app.user.dto.SignedLinkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
public class LinkSignerController {
    private final LinkSignerService linkSignerService;
    @PostMapping("/sign")
    public ResponseEntity<?> getSelfInfo(@RequestBody LinkDto linkDto) {
        System.out.println(linkDto.getLink());
        String signLink = linkSignerService.sign(linkDto.getLink());
        return ResponseEntity.status(200).body(new SignedLinkResponse(signLink));
    }

}
