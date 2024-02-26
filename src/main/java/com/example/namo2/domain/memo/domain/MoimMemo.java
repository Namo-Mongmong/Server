package com.example.namo2.domain.memo.domain;


import com.example.namo2.domain.moim.domain.MoimSchedule;
import com.example.namo2.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public boolean isLastMoimMemoLocations() {
        return moimMemoLocations != null && moimMemoLocations.size() == 1;
    }
}
