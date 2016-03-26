package org.majimena.petical.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Pet;
import org.majimena.petical.service.PetService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * ユーザーペットコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class UserPetController {

    /**
     * ペットサービス.
     */
    @Inject
    private PetService petService;

    /**
     * ユーザーが飼育しているペットを全て取得する.
     *
     * @param userId ユーザーID
     * @return レスポンスエンティティ（通常時は200）
     */
    @Timed
    @RequestMapping(value = "/users/{userId}/pets", method = RequestMethod.GET)
    public ResponseEntity<List<Pet>> get(@PathVariable String userId) {
        // IDの型チェック
        ErrorsUtils.throwIfNotIdentify(userId);

        // 顧客が参照可能かをチェックする
        List<Pet> pets = petService.getPetsByUserId(userId);
        return ResponseEntity.ok().body(pets);
    }
}
