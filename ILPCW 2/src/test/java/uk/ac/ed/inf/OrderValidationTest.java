package uk.ac.ed.inf;

import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;


import java.time.DayOfWeek;
import java.time.LocalDate;

public class OrderValidationTest extends TestCase {

    LngLat resPosition = new LngLat(0,0);
    Pizza[] resMenu1 = {
            new Pizza("Pepperoni",1100),
            new Pizza("Romana",1200),
            new Pizza("Diavola",1250)};
    Pizza[] resMenu2 = {
            new Pizza("Margherita", 1000),
            new Pizza("4 Cheeses", 1300),
            new Pizza("Stuffed Crust", 1500)};

    Pizza[] orderPizzas= {
            new Pizza("Margherita", 1000),
            new Pizza("4 Cheeses", 1300)};

    DayOfWeek[] openDays = {DayOfWeek.MONDAY,DayOfWeek.FRIDAY};

    Restaurant [] restaurants = {
            new Restaurant ("pizza1",resPosition,openDays,resMenu1),
            new Restaurant ("pizza2",resPosition,openDays,resMenu2)};

    Order orderToValidate = new Order("1",LocalDate.of(2023,10,9), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED,2400,orderPizzas,new CreditCardInformation("1234567891123456","01/24","123"));
    OrderValidation validator = new OrderValidation();

    public void testValidOrder(){
        Assert.assertEquals(OrderValidationCode.NO_ERROR,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testCardNumberNull(){
        CreditCardInformation cC = null;
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.CARD_NUMBER_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testCardNumberWithStrings(){
        CreditCardInformation cC = new CreditCardInformation("qwertyuioplkjhgf","01/24","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.CARD_NUMBER_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testShortCardNumber(){
        CreditCardInformation cC = new CreditCardInformation("123","01/24","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.CARD_NUMBER_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testLongCardNumber(){
        CreditCardInformation cC = new CreditCardInformation("12345678901234567","01/24","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.CARD_NUMBER_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testShortExpiryDate(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","01","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testExpiryDateFormat(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","01-24","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testExpiredExpiryDateMonth(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","01/23","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testExpiredExpiryDateYear(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","01/22","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testStringExpiryDateYear(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","ab/cd","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testStringExpiryDateMonthInt(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","ab/23","123");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testShortCvv(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","01/24","12");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.CVV_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testLongCvv(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","01/24","1234");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.CVV_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testStringCvv(){
        CreditCardInformation cC = new CreditCardInformation("1234567890123456","01/24","abc");
        orderToValidate.setCreditCardInformation(cC);
        Assert.assertEquals(OrderValidationCode.CVV_INVALID,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testPizzaNotOnMenus(){
        Pizza[] invalid= orderToValidate.getPizzasInOrder();
        invalid[0] = new Pizza ("Non Existent",1000);
        Assert.assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testTooManyPizzas()
    {
        Pizza[] order = {
                new Pizza("Pepperoni",1100),
                new Pizza("Romana",1200),
                new Pizza("Diavola",1250),
                new Pizza("Margherita", 1000),
                new Pizza("4 Cheeses", 1300),
                new Pizza("Stuffed Crust", 1500)};
        orderToValidate.setPizzasInOrder(order);
        Assert.assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testNoPizzas()
    {
        Pizza[] order = {};
        orderToValidate.setPizzasInOrder(order);
        Assert.assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testNoRestaurants()
    {
        Restaurant[] restaurantsNew = {};
        Assert.assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED,validator.validateOrder(orderToValidate,restaurantsNew).getOrderValidationCode());
    }

    public void testSecondPizzaNotOnMenus(){
        Pizza[] invalid= {
                new Pizza("Margherita", 1000),
                new Pizza("Non Existent", 1300)};
        orderToValidate.setPizzasInOrder(invalid);
        Assert.assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testPizzaFromMultipleRestaurants(){
        Pizza[] invalid= {
                new Pizza("Margherita", 1000),
                new Pizza ("Pepperoni",1100)};
        orderToValidate.setPizzasInOrder(invalid);
        Assert.assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testRestaurantClosed(){
        orderToValidate.setOrderDate(LocalDate.of(2023,10,14));
        Assert.assertEquals(OrderValidationCode.RESTAURANT_CLOSED,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

    public void testTotalIncorrect(){
        orderToValidate.setPriceTotalInPence(1000);
        Assert.assertEquals(OrderValidationCode.TOTAL_INCORRECT,validator.validateOrder(orderToValidate,restaurants).getOrderValidationCode());
    }

}