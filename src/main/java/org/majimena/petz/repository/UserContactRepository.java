package org.majimena.petz.repository;

import org.majimena.petz.domain.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContactRepository extends JpaRepository<UserContact, String> {

}
