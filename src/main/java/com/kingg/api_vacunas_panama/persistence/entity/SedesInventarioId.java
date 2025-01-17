package com.kingg.api_vacunas_panama.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class SedesInventarioId implements Serializable {
    @Serial
    private static final long serialVersionUID = -1833808364336740202L;
    @NotNull
    @Column(name = "sede", nullable = false)
    private UUID sede;

    @NotNull
    @Column(name = "vacuna", nullable = false)
    private UUID vacuna;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SedesInventarioId entity = (SedesInventarioId) o;
        return Objects.equals(this.vacuna, entity.vacuna) &&
                Objects.equals(this.sede, entity.sede);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vacuna, sede);
    }

}