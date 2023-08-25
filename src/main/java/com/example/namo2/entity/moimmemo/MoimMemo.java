package com.example.namo2.entity.moimmemo;


import com.example.namo2.entity.BaseTimeEntity;
import com.example.namo2.entity.moimschedule.MoimSchedule;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moim_memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MoimMemo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "moim_memo_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_schedule_id")
    private MoimSchedule moimSchedule;

    @OneToMany(mappedBy = "moimMemo", fetch = FetchType.LAZY)
    private List<MoimMemoLocation> moimMemoLocations = new ArrayList<>();

    @Builder
    public MoimMemo(Long id, MoimSchedule moimSchedule) {
        this.id = id;
        this.moimSchedule = moimSchedule;
        this.moimSchedule.registerMemo(this);
    }
}
