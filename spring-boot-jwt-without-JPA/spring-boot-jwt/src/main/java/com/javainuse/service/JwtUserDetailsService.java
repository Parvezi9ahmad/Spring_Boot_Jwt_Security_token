package com.javainuse.service;

import java.util.ArrayList;
import java.util.Collection;

import com.javainuse.dao.UserDao;
import com.javainuse.model.DAOUser;
import com.javainuse.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //for harcoded user
        /*if(username.equals("javainuse")){
            return new User("javainuse","$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
                    new ArrayList<>());
        }
        else {
            throw new UsernameNotFoundException("User is not avialble with this username :"+username);
        }
        */

         DAOUser user = userDao.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("User is not avialble with this username :"+username);
        }
        else {
           return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),
                   new ArrayList<>());
        }
    }

    public DAOUser saveuser(UserDTO user){
        DAOUser newUser=new DAOUser();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userDao.save(newUser);
    }
}
