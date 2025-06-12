package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.CreateGroupRequest;
import com.mobapp.training.dto.request.DeleteGroupRequest;
import com.mobapp.training.dto.request.EditGroupRequest;
import com.mobapp.training.dto.request.UserIdRequest;
import com.mobapp.training.logging.GroupLogMessages;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Groups API", description = "Управление группами")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);


    @GetMapping("/groups")
    @Operation(
            summary = "Получить список всех групп",
            description = "Доступно для преподавателей и администраторов"
    )
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<List<AllGroupResponse>> getAllGroups(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(GroupLogMessages.GET_ALL_GROUPS);
        return ResponseEntity.ok(groupService.getAllGroups(userDetails.getUser()));
    }

    @PostMapping("/groups/create-group")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<MessageResponse> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        logger.info(GroupLogMessages.CREATE_GROUP);
        return ResponseEntity.ok(groupService.createGroup(request, user.getUser()));
    }

    @DeleteMapping("/groups/delete-group")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<MessageResponse> deleteGroup(
            @RequestBody DeleteGroupRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        logger.info(GroupLogMessages.GROUP_DELETE_START);
        return ResponseEntity.ok(groupService.deleteGroup(request));
    }

    @PutMapping("/groups/edit-group")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<MessageResponse> editGroup(
            @RequestBody EditGroupRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        logger.info(GroupLogMessages.GROUP_EDIT_START);
        return ResponseEntity.ok(groupService.editGroup(request));
    }

    @GetMapping("/groups/add-groups")
    @PreAuthorize("hasAnyRole('TEACHER')")
    @Operation(summary = "Получить группы, не привязанные к курсу")
    public ResponseEntity<GroupsResponse> getGroupsNotInCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(GroupLogMessages.GET_GROUPS_NOT_IN_COURSE);
        return ResponseEntity.ok(groupService.getGroupsNotInCourse(userDetails.getUser()));
    }

//    @GetMapping("/groups-courses")
//    @PreAuthorize("hasAnyRole('TEACHER')")
//    public ResponseEntity<List<GroupCourseResponse>> getGroupsByTeacher(
//            @Parameter(hidden = true)
//            @AuthenticationPrincipal CustomUserDetails userDetails
//
//    ) {
//        logger.info(GroupLogMessages.GET_GROUPS_BY_TEACHER, userDetails.getId());
//        return ResponseEntity.ok(groupService.getGroupsByTeacher(userDetails.getId()));
//    }

    @GetMapping("/groups/{groupId}/students")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<GroupMembersResponse> getStudentsInGroup(
            @Parameter(description = "ID группы", required = true)
            @PathVariable UUID groupId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(GroupLogMessages.GET_STUDENTS_IN_GROUP);
        return ResponseEntity.ok(groupService.getStudentsByGroupId(groupId));
    }

    @GetMapping("/groups/{groupId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<StudentCourseStatsResponse> getStudentStatsForCourse(
            @PathVariable UUID studentId,
            @PathVariable UUID groupId
    ) {
        logger.info(GroupLogMessages.GET_STUDENT_STATS, studentId, groupId);
        return ResponseEntity.ok(groupService.getStudentStatsForGroup(studentId, groupId));
    }

    @DeleteMapping("/groups/{groupId}/students/delete-student")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<MessageResponse> deleteStudentFromCourse(
            @RequestBody UserIdRequest request,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.deleteStudentFromGroup(request.getStudentId(), groupId));
    }

    @GetMapping("/groups/{groupId}/stats")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<GroupCourseStatsResponse> getGroupStatsForCourse(
            @PathVariable UUID groupId
    ) {
        logger.info(GroupLogMessages.GET_GROUP_STATS, groupId);
        return ResponseEntity.ok(groupService.getGroupStatsForCourse(groupId));
    }
}