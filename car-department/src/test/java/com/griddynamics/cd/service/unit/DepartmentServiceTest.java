package com.griddynamics.cd.service.unit;

import com.griddynamics.cd.entity.DepartmentEntity;
import com.griddynamics.cd.entity.EmployeeEntity;
import com.griddynamics.cd.exception.EntityDeleteException;
import com.griddynamics.cd.mapper.DepartmentMapper;
import com.griddynamics.cd.model.Department;
import com.griddynamics.cd.model.create.CreateDepartmentRequest;
import com.griddynamics.cd.model.update.UpdateDepartmentRequest;
import com.griddynamics.cd.repository.DepartmentRepository;
import com.griddynamics.cd.repository.EmployeeRepository;
import com.griddynamics.cd.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    private DepartmentRepository departmentRepository;
    private DepartmentMapper departmentMapper;
    private EmployeeRepository employeeRepository;
    private DepartmentService departmentService;

    @BeforeEach
    void init() {
        departmentRepository = mock(DepartmentRepository.class);
        departmentMapper = mock(DepartmentMapper.class);
        employeeRepository = mock(EmployeeRepository.class);
        departmentService = new DepartmentService(departmentRepository, departmentMapper, employeeRepository);
    }

    @BeforeEach
    void setUp() {
        when(departmentRepository.save(any(DepartmentEntity.class)))
                .thenReturn(mock(DepartmentEntity.class));

        when(departmentRepository.findById(anyLong()))
                .thenReturn(Optional.of(mock(DepartmentEntity.class)));

        when(departmentMapper.toDepartmentEntity(any(CreateDepartmentRequest.class)))
                .thenReturn(mock(DepartmentEntity.class));

        when(departmentMapper.toDepartmentModel(any(DepartmentEntity.class)))
                .thenReturn(mock(Department.class));

        when(departmentMapper.toDepartmentEntity(any(UpdateDepartmentRequest.class), any(DepartmentEntity.class)))
                .thenReturn(mock(DepartmentEntity.class));
    }

    @Test
    void getAllDepartments_whenCallMethod_thenValidMethodCallsNumber() {
        DepartmentEntity departmentEntity1 = mock(DepartmentEntity.class);
        DepartmentEntity departmentEntity2 = mock(DepartmentEntity.class);
        DepartmentEntity departmentEntity3 = mock(DepartmentEntity.class);

        when(departmentRepository.findAll())
                .thenReturn(List.of(departmentEntity1, departmentEntity2, departmentEntity3));

        departmentService.getAllDepartments();

        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getDepartmentById_whenPassId_thenValidMethodCallsNumber() {
        departmentService.getDepartmentById(2L);
        departmentService.getDepartmentById(1L);
        departmentService.getDepartmentById(2L);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(2)).findById(2L);
        verify(departmentMapper, times(3)).toDepartmentModel(any(DepartmentEntity.class));
    }

    @Test
    void getDepartmentById_whenPassWrongId_thenThrowEntityNotFoundException() {
        when(departmentRepository.findById(2L))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                departmentService.getDepartmentById(2L));
        assertEquals(thrown.getMessage(), "Department with 2 id was not found");
    }

    @Test
    void saveDepartment_whenPassCreateDepartmentRequestWithoutEmail_thenValidMethodCallsNumber() {
        CreateDepartmentRequest createDepartmentRequest = mock(CreateDepartmentRequest.class);

        departmentService.saveDepartment(createDepartmentRequest);

        verify(departmentRepository, times(0)).existsByEmail(anyString());
        verify(departmentRepository, times(1)).save(any(DepartmentEntity.class));
        verify(departmentMapper, times(1)).toDepartmentEntity(createDepartmentRequest);
        verify(departmentMapper, times(1)).toDepartmentModel(any(DepartmentEntity.class));
    }

    @Test
    void saveDepartment_whenPassCreateDepartmentRequestWithEmail_thenValidMethodCallsNumber() {
        CreateDepartmentRequest createDepartmentRequest = mock(CreateDepartmentRequest.class);
        when(createDepartmentRequest.getEmail())
                .thenReturn("test@test");

        departmentService.saveDepartment(createDepartmentRequest);

        verify(departmentRepository, times(1)).existsByEmail("test@test");
        verify(departmentRepository, times(1)).save(any(DepartmentEntity.class));
        verify(departmentMapper, times(1)).toDepartmentEntity(createDepartmentRequest);
        verify(departmentMapper, times(1)).toDepartmentModel(any(DepartmentEntity.class));
    }

    @Test
    void saveDepartment_whenPassDifferentCreateDepartmentRequests_thenValidMethodCallsNumber() {
        CreateDepartmentRequest createDepartmentRequest1 = mock(CreateDepartmentRequest.class);
        CreateDepartmentRequest createDepartmentRequest2 = mock(CreateDepartmentRequest.class);
        when(createDepartmentRequest2.getEmail())
                .thenReturn("test@test");

        departmentService.saveDepartment(createDepartmentRequest1);
        departmentService.saveDepartment(createDepartmentRequest2);

        verify(departmentRepository, times(1)).existsByEmail("test@test");
        verify(departmentRepository, times(2)).save(any(DepartmentEntity.class));
        verify(departmentMapper, times(2)).toDepartmentModel(any(DepartmentEntity.class));
        verify(departmentMapper, times(2)).toDepartmentEntity(any(CreateDepartmentRequest.class));
    }

    @Test
    void saveDepartment_whenPassCreateDepartmentRequestWithExistingEmail_thenThrowEntityNotFoundException() {
        CreateDepartmentRequest createDepartmentRequest = mock(CreateDepartmentRequest.class);
        when(createDepartmentRequest.getEmail())
                .thenReturn("test@test");
        when(departmentRepository.existsByEmail("test@test"))
                .thenReturn(true);

        EntityExistsException thrown = assertThrows(EntityExistsException.class, () ->
                departmentService.saveDepartment(createDepartmentRequest));
        assertEquals("Department with test@test email already exist", thrown.getMessage());
    }

    @Test
    void updateDepartment_whenUpdateRequestWithoutEmployeeId_thenValidMethodCallsNumber() {
        UpdateDepartmentRequest updateDepartmentRequest = mock(UpdateDepartmentRequest.class);

        departmentService.updateDepartment(updateDepartmentRequest, 1L);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).save(any(DepartmentEntity.class));
        verify(departmentMapper, times(1)).toDepartmentEntity(eq(updateDepartmentRequest), any(DepartmentEntity.class));
        verify(departmentMapper, times(1)).toDepartmentModel(any(DepartmentEntity.class));
    }

    @Test
    void updateDepartment_whenPassUpdateDepartmentRequestWithEmail_thenValidMethodCallsNumber() {
        UpdateDepartmentRequest updateDepartmentRequest = mock(UpdateDepartmentRequest.class);
        when(updateDepartmentRequest.getEmail())
                .thenReturn("test@test");

        departmentService.updateDepartment(updateDepartmentRequest, 2L);

        verify(departmentRepository, times(1)).existsByEmailAndIdIsNot("test@test", 2L);
        verify(departmentRepository, times(1)).findById(2L);
        verify(departmentRepository, times(1)).save(any(DepartmentEntity.class));
        verify(departmentMapper, times(1)).toDepartmentEntity(eq(updateDepartmentRequest), any(DepartmentEntity.class));
        verify(departmentMapper, times(1)).toDepartmentModel(any(DepartmentEntity.class));
    }

    @Test
    void updateDepartment_whenPassWrongDepartmentId_thenThrowEntityNotFoundException() {
        UpdateDepartmentRequest updateDepartmentRequest = mock(UpdateDepartmentRequest.class);
        when(departmentRepository.findById(2L))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                departmentService.updateDepartment(updateDepartmentRequest, 2L));
        assertEquals("Department with 2 id was not found", thrown.getMessage());
    }

    @Test
    void updateDepartment_whenPassUpdateDepartmentRequestWithExistingEmail_thenThrowEntityExistsException() {
        UpdateDepartmentRequest updateDepartmentRequest = mock(UpdateDepartmentRequest.class);
        when(updateDepartmentRequest.getEmail())
                .thenReturn("test@test");
        when(departmentRepository.existsByEmailAndIdIsNot("test@test", 2L))
                .thenReturn(true);

        EntityExistsException thrown = assertThrows(EntityExistsException.class, () ->
                departmentService.updateDepartment(updateDepartmentRequest, 2L));
        assertEquals("Department with test@test email already exist", thrown.getMessage());

    }

    @Test
    void deleteDepartment_whenDeleteById_thenPassValidValue() {
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        doNothing().when(departmentRepository)
                .deleteById(captor.capture());

        when(departmentRepository.existsById(2L))
                .thenReturn(true);

        departmentService.deleteDepartment(2L);

        verify(departmentRepository, times(1)).deleteById(2L);
        assertEquals(2L, captor.getValue());
    }

    @Test
    void deleteDepartment_whenPassWrongDepartmentId_thenThrowEntityNotFoundException() {
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                departmentService.deleteDepartment(2L));
        assertEquals("Department with 2 id was not found", thrown.getMessage());
    }

    @Test
    void deleteDepartment_whenPasDepartmentIdWithDependentEmployees_thenThrowEntityDeleteException() {
        when(departmentRepository.existsById(2L))
                .thenReturn(true);
        when(employeeRepository.findAllEmployeesByDepartmentId(2L))
                .thenReturn(List.of(mock(EmployeeEntity.class), mock(EmployeeEntity.class)));

        EntityDeleteException thrown = assertThrows(EntityDeleteException.class, () ->
                departmentService.deleteDepartment(2L));
        assertEquals("Unable to delete department with id 2", thrown.getMessage());
    }
}