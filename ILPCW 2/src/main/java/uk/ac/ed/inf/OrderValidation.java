package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;

import java.time.DayOfWeek;

public class OrderValidation implements uk.ac.ed.inf.ilp.interfaces.OrderValidation {

    Restaurant restaurantInOrder;
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        //-----------------Checking for Valid Credit Card Number---------------------------

        if (orderToValidate.getCreditCardInformation() == null)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        String cardNumber = orderToValidate.getCreditCardInformation().getCreditCardNumber();
        //checking if card number is 16 digits long
        if (cardNumber.length() != 16)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        boolean nonIntegerFound = false;
        int count = 0;
        //loops through all characters in card number to make sure they are all digits
        while (nonIntegerFound == false)
        {
            if (!(Character.isDigit(cardNumber.charAt(count))))
            {
                nonIntegerFound = true;
            }
            else if (count == 15)
            {
                break;
            }
            count++;
        }

        //returns invalid order status if a non integer is found in the number
        if (nonIntegerFound)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        //---------------------Checking for Valid Expiry Date------------------------

        String expiryDate = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        //checking if card expiry date is 5 characters long
        if (expiryDate.length() != 5)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }
        String m = expiryDate.substring(0,2);
        String y = expiryDate.substring(3,5);
        String dash = expiryDate.substring(2,3);

        //checks if the expiry date is in the correct format
        if (!dash.equals("/"))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        int year = 0;
        //checking year in expiry is made up of digits
        if (Character.isDigit(y.charAt(0)) && Character.isDigit(y.charAt(1)))
        {
            year = Integer.parseInt(y);
        }
        else
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        int orderYear = orderToValidate.getOrderDate().getYear();

        //checking if expiry date year is less than the order year
        if (year < (orderYear % 100))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }
        //checking if expiry date year is equal to the order year
        else if (year == (orderYear % 100))
        {
            int month = 0;
            int orderMonth = orderToValidate.getOrderDate().getMonthValue();
            //checking if card expiry month is made up of integers
            if (Character.isDigit(m.charAt(0)) && Character.isDigit(m.charAt(1)))
            {
                month = Integer.parseInt(m);
            }
            else
            {
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                return orderToValidate;
            }

            //checking if month has past or month is larger than 12
            if ((month < orderMonth) || (month > 12))
            {
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                return orderToValidate;
            }
        }

        //---------------------------Checking for Valid CVV--------------------------------------

        String cvv = orderToValidate.getCreditCardInformation().getCvv();
        //checking if CVV is 3 characters long
        if (cvv.length() != 3)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        nonIntegerFound = false;
        count = 0;
        //checking all characters are digits
        while (!nonIntegerFound)
        {
            if (!(Character.isDigit(cvv.charAt(count))))
            {
                nonIntegerFound = true;
            }
            else if (count == 2)
            {
                break;
            }
            count++;
        }

        if (nonIntegerFound)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        //---------------------Checking for Valid Number of Pizzas in Order----------------------------

        if ((orderToValidate.getPizzasInOrder().length > 4) || (orderToValidate.getPizzasInOrder().length < 1))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return orderToValidate;
        }

        int restaurantInOrderIndex = -1;
        int numOfRes = 0;
        int numOfPizzasFound = 0;
        //verifying number of restaurants ordered from
        //going through every restaurant's menu in order and checking what restaurant was ordered from
        for (int i = 0; i < definedRestaurants.length; i++)
        {
            for (Pizza pizzaOnMenu : definedRestaurants[i].menu())
            {
                for (int x = 0; x < orderToValidate.getPizzasInOrder().length; x++) {
                    //checking if pizza is in menu and restaurant hasn't been stored already
                    if (pizzaOnMenu.name().equals(orderToValidate.getPizzasInOrder()[x].name()))
                    {
                        numOfPizzasFound ++;
                        if (! (restaurantInOrderIndex == i))
                        {
                            numOfRes ++;
                        }
                        restaurantInOrderIndex = i;
                    }
                }
            }
        }

        //----------------------Checking if Pizzas ordered exist----------------------
        if (restaurantInOrderIndex == -1)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        if (numOfPizzasFound != orderToValidate.getPizzasInOrder().length)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        //----------------------Checking if Pizzas were ordered from multiple restaurants ----------------------

        if (numOfRes > 1)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            return orderToValidate;
        }


        restaurantInOrder = definedRestaurants[restaurantInOrderIndex];

        //----------------------Checking if Pizzas were ordered from open restaurants ----------------------

        boolean resOpen = false;
        DayOfWeek currentDay = orderToValidate.getOrderDate().getDayOfWeek();
        //validating if restaurant is open
        for (int i = 0; i < restaurantInOrder.openingDays().length; i++)
        {
            if (currentDay.equals(restaurantInOrder.openingDays()[i]))
            {
                resOpen = true;
            }
        }

        if (!resOpen)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return orderToValidate;
        }

        //----------------------Checking if order total is correct----------------------

        int total = SystemConstants.ORDER_CHARGE_IN_PENCE;
        for (int x = 0; x < orderToValidate.getPizzasInOrder().length; x++)
        {
            total = total + orderToValidate.getPizzasInOrder()[x].priceInPence();
        }

        if (!(total == orderToValidate.getPriceTotalInPence()))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        //----------------------Makes order validations code valid if no errors found----------------------

        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;
    }

    Restaurant getOrderRestaurant ()
    {
        return restaurantInOrder;
    }
}
