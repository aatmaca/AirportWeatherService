package weather.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A collected point, including some information about the range of collected
 * values
 *
 * @author code test administrator
 */
public class DataPoint {

	public double mean = 0.0;

	public double first = 0;

	public double second = 0.0;

	public double third = 0;

	public int count = 0;

	/** private constructor, use the builder to create this object */
	private DataPoint() {
	}

	protected DataPoint(double first, double mean, double second, double third, int count) {
		this.setFirst(first);
		this.setMean(mean);
		this.setSecond(second);
		this.setThird(third);
		this.setCount(count);
	}

	/** 1st quartile -- useful as a lower bound */
	public double getFirst() {
		return first;
	}

	protected void setFirst(double first) {
		this.first = first;
	}

	/** the mean of the observations */
	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	/** 2nd quartile -- median value */
	public double getSecond() {
		return second;
	}

	protected void setSecond(double second) {
		this.second = second;
	}

	/** 3rd quartile value -- less noisy upper value */
	public double getThird() {
		return third;
	}

	protected void setThird(double third) {
		this.third = third;
	}

	/** the total number of measurements */
	public int getCount() {
		return count;
	}

	protected void setCount(int count) {
		this.count = count;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

	public boolean equals(Object that) {
		return this.toString().equals(that.toString());
	}

	static public class Builder {
		double first;
		double mean;
		double median;
		double last;
		int count;

		public Builder() {
		}

		public Builder withFirst(double first) {
			this.first = first;
			return this;
		}

		public Builder withMean(double mean) {
			this.mean = mean;
			return this;
		}

		public Builder withMedian(double median) {
			this.median = median;
			return this;
		}

		public Builder withCount(int count) {
			this.count = count;
			return this;
		}

		public Builder withLast(double last) {
			this.last = last;
			return this;
		}

		public DataPoint build() {
			return new DataPoint(this.first, this.mean, this.median, this.last, this.count);
		}
	}
}
