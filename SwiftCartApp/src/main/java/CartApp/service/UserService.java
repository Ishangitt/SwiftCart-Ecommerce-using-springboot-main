package CartApp.service;

import CartApp.dto.request.UserRequestDTO;
import CartApp.dto.response.UserResponseDTO;
import CartApp.dto.request.UpdateUserRequestDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByEmail(String email);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> getActiveUsers();

    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO userRequestDTO);

    void activateUser(Long id);

    void deactivateUser(Long id);

    List<UserResponseDTO> searchUsers(String keyword);

    boolean existsByEmail(String email);
}
