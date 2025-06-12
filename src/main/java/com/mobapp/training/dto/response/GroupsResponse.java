package com.mobapp.training.dto.response;

import com.mobapp.training.models.StudentGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class GroupsResponse {
    private List<GroupResponse> groups;

    public GroupsResponse() {
        this.groups = new ArrayList<>();
    }
}
