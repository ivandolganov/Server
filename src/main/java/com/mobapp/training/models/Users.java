package com.mobapp.training.models;

import com.mobapp.training.enums.Gender;
import com.mobapp.training.enums.UserRole;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
public class Users {
    @Getter
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Id
    @Getter
    @Column(name = "id", nullable = false)
    private UUID id;

    @Getter
    @Setter
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

    @Getter
    @Setter
    @Column(name = "password_hash", nullable = false, length = Integer.MAX_VALUE)
    private String passwordHash;

    @ColumnDefault("''")
    @Getter
    @Setter
    @Column(name = "first_name", nullable = false, length = 55)
    private String firstName;

    @ColumnDefault("''")
    @Getter
    @Setter
    @Column(name = "last_name", nullable = false, length = Integer.MAX_VALUE)
    private String lastName;

    @ColumnDefault("''")
    @Getter
    @Setter
    @Column(name = "patronymic", nullable = false, length = Integer.MAX_VALUE)
    private String patronymic;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::gender_enum")
    @Column(name = "gender", length = Integer.MAX_VALUE)
    private Gender gender;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::role_enum")
    @Column(name = "role", nullable = false, length = Integer.MAX_VALUE)
    private UserRole role;

    @Getter
    @Setter
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @ManyToMany
    @Getter
    @JoinTable(
            name = "student_group_members",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<StudentGroup> studentGroups = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Theme> createdThemes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "teacher")
    @Getter
    private Set<Course> teacherCourses = new HashSet<>();

    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }
    public void addGroup(StudentGroup group) {
        studentGroups.add(group);
    }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @Getter
    private Set<StudentGroup> createdGroups = new HashSet<>();
}
