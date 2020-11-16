package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.data.dto.CommentResponseDTO;
import aut.bme.CAFFStore.data.dto.UserDetailsDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.User;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserService {

    @Autowired
    CaffService caffService;

    public UserDetailsDTO createUserDetailsDTO(User user) {
        return new UserDetailsDTO(
                user.getId(),
                user.getPersonName(),
                user.getEmail(),
                user.getComments().stream().map(CommentResponseDTO::createCommentDTO).collect(Collectors.toList()),
                caffService.getMultipleCaffDTOsById(Lists.newArrayList(user.getCaffs().stream().map(Caff::getId).collect(Collectors.toList()))));
    }
}
