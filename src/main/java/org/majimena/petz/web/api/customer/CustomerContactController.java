package org.majimena.petz.web.api.customer;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CustomerContactController {

    @Inject
    protected UserService userService;

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/{userId}/contacts", method = RequestMethod.GET)
    public ResponseEntity<UserContact> get(@PathVariable String clinicId, @PathVariable String userId) {
        // TODO 自分の顧客以外は見られないように権限チェック
        // クリニックの顧客の連絡先を登録・更新
        Optional<UserContact> contact = userService.getUserContactByUserId(userId);
        return contact
                .map(c -> ResponseEntity.ok(c))
                .orElse(ResponseEntity.ok().body(new UserContact()));
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/{userId}/contacts", method = RequestMethod.POST)
    public ResponseEntity<?> post(@PathVariable String clinicId, @PathVariable String userId, @RequestBody @Valid UserContact registry) {
        // TODO 自分の顧客以外は見られないように権限チェック
        // クリニックの顧客の連絡先を登録・更新
        registry.setId(userId);
        userService.saveUserContact(registry);
        return ResponseEntity.created(URI.create("/api/v1/users/" + userId + "/contact")).build();
    }
}
