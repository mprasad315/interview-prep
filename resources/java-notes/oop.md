# SOLID Principles
## SRP - Single Responsibility Principle
Every class should only have 1 reason to change
Instead of cramming multiple unrelated methods into a class, split into multiple classes
Promotes readability, maintainability, testability, reusability
Breaking down complex class into smaller classes is good for modularity and separation of concerns

## OCP - Open-Closed Principle
Software entities should be open for extension but closed for modification
For example, instead of having a PayCalculator class that checks Employee instanceof Manager/Engineer/etc., turn Employee into an interface with a method calculatePay, which each instance would implement. Then the PayCalculator class delegates to the employee
```
interface Employee {
    double calculatePay();
}

class Manager implements Employee {
    @Override
    public double calculatePay() {
        // specific logic
    }
}

class Engineer implements Employee {
    @Override
    public double calculatePay() {
        // specific logic
    }
}

class PayCalculator {
    public double calculatePay(Employee e) {
        return e.calculatePay();
    }
}
```
Allows for modification within each Employee class without affecting others.

## LSP - Liskov Substitution Principle
Objects of a superclass should be replaceable with objects of its subclasses without affecting the correctness of the program
In simple english, code that works for a class should work for any of its subtypes

## ISP - Interface Segregation Principle
Many client-specific interfaces are better than one general purpose interface
Make interfaces as small as possible so that each object can implement only the relevant interfaces, changes to one interface are less likely to affect classes that don't affect that interface
Decouples code and promotes neatness

## DIP - Dependency Inversion Principle
High-level modules should depend on abstractions or interfaces, not low-level modules
For example
```
class Employee {
    public void notifyPromotion(EmailSender sender) {
        emailSender.sendPromotionEmail(this);
    }
}
```
In this case, Employee is tightly coupled to the EmailSender class. Changing the way notifications are sent, you would need to modify the Employee class itself.
Using DIP this would be rewritten as
```
interface Notifier {
    void sendNotification(Employee employee);
}
class EmailSender implements Notifier { /* ... */ }
class SMSSender implements Notifier { /* ... */ }
class Employee {
    public void notifyPromotion(Notifier notifier) {
        notifier.sendNotification(this);
    }
}
```