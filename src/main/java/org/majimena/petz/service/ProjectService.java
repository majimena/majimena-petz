package org.majimena.petz.service;

import org.majimena.petz.domain.Project;
import org.majimena.petz.domain.project.ProjectCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Created by todoken on 2015/06/06.
 */
public interface ProjectService {

    Optional<Project> getProjectById(Long projectId);

    Page<Project> getProjects(ProjectCriteria criteria, Pageable pageable);

    Optional<Project> saveProject(Project project);

    Optional<Project> updateProject(Project project);

    void deleteProject(Long projectId);

}
