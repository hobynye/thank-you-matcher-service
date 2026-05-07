package org.hobynye.tym.match;

import jakarta.persistence.*;
import org.hobynye.tym.ambassador.Ambassador;
import org.hobynye.tym.seminar.Seminar;
import org.hobynye.tym.supporter.Supporter;
import java.util.UUID;

@Entity
@Table(name = "match_result", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ambassador_id", "supporter_id"})
})
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seminar_id", nullable = false)
    private Seminar seminar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ambassador_id", nullable = false)
    private Ambassador ambassador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supporter_id", nullable = false)
    private Supporter supporter;

    @Column(nullable = false)
    private boolean mandatory;

    public UUID getId() { return id; }

    public Seminar getSeminar() { return seminar; }
    public void setSeminar(Seminar seminar) { this.seminar = seminar; }

    public Ambassador getAmbassador() { return ambassador; }
    public void setAmbassador(Ambassador ambassador) { this.ambassador = ambassador; }

    public Supporter getSupporter() { return supporter; }
    public void setSupporter(Supporter supporter) { this.supporter = supporter; }

    public boolean isMandatory() { return mandatory; }
    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }
}
