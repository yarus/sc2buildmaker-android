package com.sc2toolslab.sc2bm.datacontracts;

public class GlobalConstantsInfo {
	private Integer MineralsPatchesPerBase = 0;
	private Integer MineralsPerMinuteFrom3WorkersPerPatch = 0;
	private Integer MineralsPerMinuteFrom2WorkersPerPatch = 0;
	private Integer MineralsPerMinuteFrom1WorkerPerPatch = 0;
	private Integer MineralsPerMinuteFrom1Mule = 0;
	private Integer GasPatchesPerBase = 0;
	private Integer GasPerMinuteFrom3WorkersPerPatch = 0;
	private Integer GasPerMinuteFrom2WorkersPerPatch = 0;
	private Integer GasPerMinuteFrom1WorkerPerPatch = 0;
	private Integer MaximumPeriodInSecondsForBuildPrediction = 0;

	@Override
	public int hashCode() {
		int hashCode = MineralsPatchesPerBase;
		hashCode = (hashCode*397) ^ GasPatchesPerBase;
		hashCode = (hashCode*397) ^ MaximumPeriodInSecondsForBuildPrediction;
		hashCode = (hashCode*397) ^ MineralsPerMinuteFrom3WorkersPerPatch;
		hashCode = (hashCode*397) ^ MineralsPerMinuteFrom2WorkersPerPatch;
		hashCode = (hashCode*397) ^ MineralsPerMinuteFrom1WorkerPerPatch;
		hashCode = (hashCode*397) ^ GasPerMinuteFrom3WorkersPerPatch;
		hashCode = (hashCode*397) ^ GasPerMinuteFrom2WorkersPerPatch;
		hashCode = (hashCode*397) ^ GasPerMinuteFrom1WorkerPerPatch;
		hashCode = (hashCode*397) ^ MineralsPerMinuteFrom1Mule;

		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (o.getClass() != this.getClass()) return false;

		return equals((GlobalConstantsInfo)o);
	}

	protected boolean equals(GlobalConstantsInfo s) {
		return MineralsPatchesPerBase == s.getMineralsPatchesPerBase() && GasPatchesPerBase == s.getGasPatchesPerBase()
				&& MaximumPeriodInSecondsForBuildPrediction == s.getMaximumPeriodInSecondsForBuildPrediction() && MineralsPerMinuteFrom3WorkersPerPatch == s.getMineralsPerMinuteFrom3WorkersPerPatch()
				&& MineralsPerMinuteFrom2WorkersPerPatch == s.getMineralsPerMinuteFrom2WorkersPerPatch() && MineralsPerMinuteFrom1WorkerPerPatch == s.getMineralsPerMinuteFrom1WorkerPerPatch()
				&& GasPerMinuteFrom3WorkersPerPatch == s.getGasPerMinuteFrom3WorkersPerPatch() && GasPerMinuteFrom2WorkersPerPatch == s.getGasPerMinuteFrom2WorkersPerPatch() && GasPerMinuteFrom1WorkerPerPatch == s.getGasPerMinuteFrom1WorkerPerPatch()
				&& MineralsPerMinuteFrom1Mule == s.getMineralsPerMinuteFrom1Mule();
	}

	public Integer getMineralsPatchesPerBase() {
		return MineralsPatchesPerBase;
	}

	public void setMineralsPatchesPerBase(Integer mineralsPatchesPerBase) {
		this.MineralsPatchesPerBase = mineralsPatchesPerBase;
	}

	public Integer getMineralsPerMinuteFrom3WorkersPerPatch() {
		return MineralsPerMinuteFrom3WorkersPerPatch;
	}

	public void setMineralsPerMinuteFrom3WorkersPerPatch(Integer mineralsPerMinuteFrom3WorkersPerPatch) {
		this.MineralsPerMinuteFrom3WorkersPerPatch = mineralsPerMinuteFrom3WorkersPerPatch;
	}

	public Integer getMineralsPerMinuteFrom2WorkersPerPatch() {
		return MineralsPerMinuteFrom2WorkersPerPatch;
	}

	public void setMineralsPerMinuteFrom2WorkersPerPatch(Integer mineralsPerMinuteFrom2WorkersPerPatch) {
		this.MineralsPerMinuteFrom2WorkersPerPatch = mineralsPerMinuteFrom2WorkersPerPatch;
	}

	public Integer getMineralsPerMinuteFrom1WorkerPerPatch() {
		return MineralsPerMinuteFrom1WorkerPerPatch;
	}

	public void setMineralsPerMinuteFrom1WorkerPerPatch(Integer mineralsPerMinuteFrom1WorkerPerPatch) {
		this.MineralsPerMinuteFrom1WorkerPerPatch = mineralsPerMinuteFrom1WorkerPerPatch;
	}

	public Integer getMineralsPerMinuteFrom1Mule() {
		return MineralsPerMinuteFrom1Mule;
	}

	public void setMineralsPerMinuteFrom1Mule(Integer mineralsPerMinuteFrom1Mule) {
		this.MineralsPerMinuteFrom1Mule = mineralsPerMinuteFrom1Mule;
	}

	public Integer getGasPatchesPerBase() {
		return GasPatchesPerBase;
	}

	public void setGasPatchesPerBase(Integer gasPatchesPerBase) {
		this.GasPatchesPerBase = gasPatchesPerBase;
	}

	public Integer getGasPerMinuteFrom3WorkersPerPatch() {
		return GasPerMinuteFrom3WorkersPerPatch;
	}

	public void setGasPerMinuteFrom3WorkersPerPatch(Integer gasPerMinuteFrom3WorkersPerPatch) {
		this.GasPerMinuteFrom3WorkersPerPatch = gasPerMinuteFrom3WorkersPerPatch;
	}

	public Integer getGasPerMinuteFrom2WorkersPerPatch() {
		return GasPerMinuteFrom2WorkersPerPatch;
	}

	public void setGasPerMinuteFrom2WorkersPerPatch(Integer gasPerMinuteFrom2WorkersPerPatch) {
		this.GasPerMinuteFrom2WorkersPerPatch = gasPerMinuteFrom2WorkersPerPatch;
	}

	public Integer getGasPerMinuteFrom1WorkerPerPatch() {
		return GasPerMinuteFrom1WorkerPerPatch;
	}

	public void setGasPerMinuteFrom1WorkerPerPatch(Integer gasPerMinuteFrom1WorkerPerPatch) {
		this.GasPerMinuteFrom1WorkerPerPatch = gasPerMinuteFrom1WorkerPerPatch;
	}

	public Integer getMaximumPeriodInSecondsForBuildPrediction() {
		return MaximumPeriodInSecondsForBuildPrediction;
	}

	public void setMaximumPeriodInSecondsForBuildPrediction(Integer maximumPeriodInSecondsForBuildPrediction) {
		this.MaximumPeriodInSecondsForBuildPrediction = maximumPeriodInSecondsForBuildPrediction;
	}
}
