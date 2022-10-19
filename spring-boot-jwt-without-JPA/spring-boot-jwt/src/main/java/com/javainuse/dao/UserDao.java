package com.javainuse.dao;

import com.javainuse.model.DAOUser;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<DAOUser,Integer> {
    DAOUser findByUsername(String username);
}
