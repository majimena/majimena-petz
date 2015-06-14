package org.majimena.petz.repository;

import org.majimena.petz.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Project entity.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
