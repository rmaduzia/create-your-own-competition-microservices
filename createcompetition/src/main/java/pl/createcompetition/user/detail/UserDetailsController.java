package pl.createcompetition.user.detail;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Controller
@RequestMapping(value = "/user_details")
public class UserDetailsController {

    private final UserDetailService userDetailService;


    @GetMapping
    @ResponseBody
    public PagedResponseDto<?> searchUserDetail(@RequestParam(value = "search") @NotBlank String search,
                                               @Valid PaginationInfoRequest paginationInfoRequest) {
        return userDetailService.searchUser(search, paginationInfoRequest);

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<?> addUserDetail(@Valid @RequestBody UserDetail userDetail,
                                           @CurrentUser UserPrincipal userPrincipal) {

        return userDetailService.addUserDetail(userDetail, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("{userName}")
    public ResponseEntity<?> updateUserDetail(@Valid @RequestBody UserDetail userDetail,
                                              @CurrentUser UserPrincipal userPrincipal,
                                              @PathVariable String userName) {

        return userDetailService.updateUserDetail(userName, userDetail, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{userName}")
    public ResponseEntity<?> deleteUserDetail(@PathVariable String userName,
                                              @CurrentUser UserPrincipal userPrincipal) {

        return userDetailService.deleteUserDetail(userName, userPrincipal);
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("{userName}/addOpinion")
    public ResponseEntity<?> addOpinionAboutUser(@PathVariable String userName,
                                                 @RequestBody String opinionContent,
                                                 @CurrentUser UserPrincipal userPrincipal) {

        return userDetailService.addOpinionAboutUser(userName, opinionContent, userPrincipal);
    }
}
