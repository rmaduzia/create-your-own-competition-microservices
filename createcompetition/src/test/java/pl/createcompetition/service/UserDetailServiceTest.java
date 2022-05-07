package pl.createcompetition.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.*;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.user.detail.UserDetail;
import pl.createcompetition.user.detail.UserDetailRepository;
import pl.createcompetition.user.UserRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.util.query.GetQueryImplService;
import pl.createcompetition.user.User;
import pl.createcompetition.user.detail.UserDetailService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserDetailRepository userDetailRepository;
    @InjectMocks
    UserDetailService userDetailService;
    @Mock
    GetQueryImplService getQueryImplService;

    User user;
    UserDetail userDetail;
    UserDetail.UserDetailDto userDetailDto;
    UserPrincipal userPrincipal;

    List<UserDetail.UserDetailDto> userDetailDtoList;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .password("Password%123")
                .id(1L).provider(AuthProvider.local)
                .email("test@mail.com").emailVerified(true).build();

        userPrincipal = UserPrincipal.create(user);

        userDetail = UserDetail.builder()
                .id(1L)
                .user(user)
                .userName("test@mail.com")
                .age(15)
                .city("Gdynia")
                .gender(Gender.FEMALE).build();


        userDetailDto = UserDetail.UserDetailDto.builder().city("Gdynia").build();

        userDetailDtoList = new ArrayList<>();
    }

    @Test
    public void shouldReturnUsersDetails() {
        PaginationInfoRequest paginationInfoRequest = new PaginationInfoRequest(0,10);
        PageModel pageModel = new PageModel(0,10,1,1, true);

        userDetailDtoList.add(userDetailDto);

        PagedResponseDto pagedResponseDto = PagedResponseDtoBuilder.create().listDto(userDetailDtoList).entityPage(pageModel).build();

        when(getQueryImplService.execute(UserDetail.class,"search=city:Gdynia",paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize())).thenReturn(pagedResponseDto);

        assertEquals(userDetailService.searchUser("search=city:Gdynia",paginationInfoRequest), pagedResponseDto);
    }

    @Test
    public void shouldAddUserDetail() {

        when(userRepository.findByIdAndEmail(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(Optional.of(user));
        when(userDetailRepository.save(ArgumentMatchers.any(UserDetail.class))).thenReturn(userDetail);

        ResponseEntity<?> response = userDetailService.addUserDetail(userDetail, userPrincipal);

        verify(userDetailRepository, times(1)).save(userDetail);
        verify(userRepository, times(1)).findByIdAndEmail(userPrincipal.getId(), userPrincipal.getEmail());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), userDetail);

    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {


        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userDetailService.addUserDetail(userDetail, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(userRepository, times(1)).findByIdAndEmail(userPrincipal.getId(), userPrincipal.getEmail());
        assertEquals("UserProfile not found with ID : '"+ userPrincipal.getUsername()+"'", exception.getMessage());
    }

    @Test
    public void shouldUpdateUserDetail() {

        when(userRepository.findByIdAndEmail(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(Optional.of(user));
        when(userDetailRepository.save(ArgumentMatchers.any(UserDetail.class))).thenReturn(userDetail);

        ResponseEntity<?> response = userDetailService.updateUserDetail(userDetail.getUserName(), userDetail, userPrincipal);

        verify(userDetailRepository, times(1)).save(userDetail);
        verify(userRepository, times(1)).findByIdAndEmail(userPrincipal.getId(), userPrincipal.getEmail());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), userDetail);
    }

    @Test
    public void shouldDeleteUserDetail() {

        when(userRepository.findByIdAndEmail(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userDetailService.deleteUserDetail(userDetail.getUserName(), userPrincipal);

        verify(userDetailRepository, times(1)).deleteById(userPrincipal.getId());
        verify(userRepository, times(1)).findByIdAndEmail(userPrincipal.getId(), userPrincipal.getEmail());

        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }
}