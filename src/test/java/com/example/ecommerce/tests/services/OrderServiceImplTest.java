package com.example.ecommerce.tests.services;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerce.exceptions.BadDataResponse;
import com.example.ecommerce.exceptions.ResourceNotFound;
import com.example.ecommerce.exceptions.ServiceUnavailable;
import com.example.ecommerce.models.Item;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.repositories.OrderRepository;
import com.example.ecommerce.services.OrderService;
import com.example.ecommerce.services.OrderServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;

/**
 * Contains the unit tests for the OrderServiceImpl.
 */
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private OrderService orderService;
  private AutoCloseable closeable;
  private DataLoader dataLoader;

  @InjectMocks
  OrderServiceImpl orderServiceImpl;
  Item item;
  Order order;
  List<Order> orderList = new ArrayList<>();

  @BeforeEach
  public void setUp() throws Exception {
    this.dataLoader = new DataLoader();
    closeable = MockitoAnnotations.openMocks(this);
    item = new Item(1L, 23,
        order);
    order = new Order(1L, dataLoader.createDate("2022-04-22"), new ArrayList<>(),
        new BigDecimal("23.99"));
    order.addItemToOrder(item);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void queryOrdersReturnsCorrectOrders() {
    when(orderRepository.findAll()).thenReturn(orderList);
    List<Order> result = orderServiceImpl.queryOrders(new Order());
    assertEquals(orderList, result);
  }

  @Test
  public void queryOrdersEmptyOrderReturnsCorrectOrders() {
    when(orderRepository.findAll()).thenReturn(orderList);
    Order emptyOrder = new Order(null, null, null, null);
    List<Order> result = orderServiceImpl.queryOrders(emptyOrder);
    assertEquals(orderList, result);
  }

  @Test
  public void queryOrdersByExample() {
    when(orderRepository.findAll(any(Example.class))).thenReturn(orderList);
    List<Order> result = orderServiceImpl.queryOrders(order);
    assertEquals(orderList, result);
  }

  @Test
  public void queryOrdersThrowsServiceUnavailable() {
    doThrow(ServiceUnavailable.class).when(orderRepository).findAll(any(Example.class));
    assertThrows(ServiceUnavailable.class,
        () -> orderServiceImpl.queryOrders(new Order()));
  }

  @Test
  public void getOrderByExistingIdReturnsOrder() {
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
    Order result = orderServiceImpl.getOrderById(1L);
    assertEquals(order, result);
  }

  @Test
  public void getOrderByNonExistentIdThrowsNotFound() {
    when(orderRepository.findById(any(Long.class))).thenReturn(empty());
    assertThrows(ResourceNotFound.class, () -> orderServiceImpl.getOrderById(999L));
  }

  @Test
  public void getOrderByNegativeIdThrowsBadDataResponse() {
    when(orderRepository.findById(any(Long.class))).thenThrow(BadDataResponse.class);
    assertThrows(BadDataResponse.class, () -> orderServiceImpl.getOrderById(-18L));
  }

  @Test
  public void getOrderByIdThrowsServiceUnavailable() {
    doThrow(ServiceUnavailable.class).when(orderRepository).findById(any(Long.class));
    assertThrows(ServiceUnavailable.class, () -> orderServiceImpl.getOrderById(1L));
  }

  @Test
  public void postOrderWithValidBodyCreatesSuccessfully() {
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    Order result = orderServiceImpl.addOrder(new Order());
    assertEquals(order, result);
  }

  @Test
  public void postOrderThrowsServiceUnavailable() {
    doThrow(ServiceUnavailable.class).when(orderRepository).save(any(Order.class));
    assertThrows(ServiceUnavailable.class,
        () -> orderServiceImpl.addOrder(new Order()));
  }

  @Test
  public void updateOrderWithValidIdAndBodyReturnsOrderSuccessfully() {
    when(orderRepository.existsById(any(Long.class))).thenReturn(true);
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    Order result = orderServiceImpl.updateOrderById(1L, new Order());
    assertEquals(order, result);
  }

  @Test
  public void updateOrderWithNonExistentIdThrowsNotFound() {
    when(orderRepository.existsById(any(Long.class))).thenReturn(false);
    assertThrows(ResourceNotFound.class,
        () -> orderServiceImpl.updateOrderById(999L, new Order()));
  }

  @Test
  public void updateOrderWithNegativeIdThrowsBadDataResponse() {
    assertThrows(BadDataResponse.class,
        () -> orderServiceImpl.updateOrderById(-8L, new Order()));
  }

  @Test
  public void updateOrderThrowsServiceUnavailable() {
    when(orderRepository.existsById(any(Long.class))).thenReturn(true);
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
    doThrow(ServiceUnavailable.class).when(orderRepository).save(any(Order.class));
    assertThrows(ServiceUnavailable.class,
        () -> orderServiceImpl.updateOrderById(1L, new Order()));
  }

  @Test
  public void deleteOrderByExistentIdReturns204NoContent() {
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
    orderServiceImpl.deleteOrderById(1L);
    verify(orderRepository).deleteById(any());
  }

  @Test
  public void deleteOrderByNonExistentIdThrows404NotFound() {
    when(orderRepository.findById(any(Long.class))).thenReturn(empty());
    assertThrows(ResourceNotFound.class, () -> orderServiceImpl.deleteOrderById(999L));
  }

  @Test
  public void deleteOrderByNegativeIdThrows400BadDataResponse() {
    assertThrows(BadDataResponse.class, () -> orderServiceImpl.deleteOrderById(-9L));
  }

  @Test
  public void deleteOrderThrowsServiceUnavailable() {
    when(orderRepository.existsById(any(Long.class))).thenReturn(true);
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
    doThrow(ServiceUnavailable.class).when(orderRepository).deleteById(any(Long.class));
    assertThrows(ServiceUnavailable.class, () -> orderServiceImpl.deleteOrderById(1L));
  }

  @Test
  public void deleteEmptyOrderThrowsResourceNotFound() {
    when(orderRepository.findById(any(Long.class))).thenReturn(empty());
    assertThrows(ResourceNotFound.class, () -> orderServiceImpl.deleteOrderById(40L));
  }
}