package faang.school.servicetemplate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity(name = "calculation")
public class CalculationJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "calculation_id")
    @SequenceGenerator(name = "calculation_id", sequenceName = "calculation_id_seq", allocationSize = 1)
    private int id;

    private int a;
    private int b;

    // Что бы сохранялось именно имя enum'а
    @Enumerated(EnumType.STRING)

    private CalculationType calculationType;
    private int result;

    public CalculationJpa() {
    }

    public CalculationJpa(int a, int b, CalculationType calculationType, int result) {
        this.a = a;
        this.b = b;
        this.calculationType = calculationType;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public CalculationType getCalculationType() {
        return calculationType;
    }

    public int getResult() {
        return result;
    }
}
