package pl.createcompetition.user.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.exception.BadRequestException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.user.User;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.user.UserRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.util.query.GetQueryImplService;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserDetailService {

    private final UserDetailRepository userDetailRepository;
    private final UserRepository userRepository;
    private final GetQueryImplService<UserDetail,?> queryUserDetailService;

    public PagedResponseDto<?> searchUser(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryUserDetailService.execute(UserDetail.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }


    public ResponseEntity<?> addUserDetail(UserDetail userDetail, UserPrincipal userPrincipal)  {

        User foundUser = findUserByIdAndEmail(userPrincipal);

        userDetail.setUser(foundUser);
        userDetail.setId(foundUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailRepository.save(userDetail));
    }

    public ResponseEntity<?> updateUserDetail(String userName, UserDetail userDetail, UserPrincipal userPrincipal){

        if (!userDetail.getUserName().equals(userName)) {
            throw new BadRequestException("User Name doesn't match with UserDetail object");
        }

        findUserByIdAndEmail(userPrincipal);
        userDetail.setId(userPrincipal.getId());

        return ResponseEntity.ok(userDetailRepository.save(userDetail));
    }

    public ResponseEntity<?> deleteUserDetail(String userName, UserPrincipal userPrincipal) {

        findUserByIdAndEmail(userPrincipal);

        if (userPrincipal.getUsername().equals(userName)) {
                userDetailRepository.deleteById(userPrincipal.getId());
            return ResponseEntity.noContent().build();
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    public ResponseEntity<?> addOpinionAboutUser(String recipientUserName, String opinionContent, UserPrincipal userPrincipal) {

        UserDetail foundUserDetail = findUserByUserName(recipientUserName);

        foundUserDetail.setOpinionAboutUser(Collections.singletonMap(userPrincipal.getUsername(), opinionContent));

        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailRepository.save(foundUserDetail));
    }

    private User findUserByIdAndEmail(UserPrincipal userPrincipal) {
        return userRepository.findByIdAndEmail(userPrincipal.getId(), userPrincipal.getUsername()).orElseThrow(() ->
                new ResourceNotFoundException("UserProfile", "ID", userPrincipal.getUsername()));
    }

    private UserDetail findUserByUserName(String userName) {
        return userDetailRepository.findByUserName(userName).orElseThrow(() ->
                new BadRequestException(userName + " does not exists"));
    }
}
