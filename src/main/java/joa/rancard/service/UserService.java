package joa.rancard.service;

import joa.rancard.model.User;
import joa.rancard.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public User get(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
