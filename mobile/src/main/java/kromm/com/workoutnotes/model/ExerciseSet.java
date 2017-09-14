package kromm.com.workoutnotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kromm.com.workoutnotes.util.Disposable;

/**
 * Created by Steffen on 17.06.2016.
 */
public class ExerciseSet implements Disposable, Parcelable
{
    private int repetitions;
    private float weight;
    private final Exercise exercise;

    @Override
    public void dispose()
    {
        exercise.removeSet(this);
    }

    public ExerciseSet(int repetitions, float weight, Exercise exercise)
    {
        this.repetitions = repetitions;
        this.weight = weight;
        this.exercise = exercise;
    }

    public ExerciseSet(Parcel parcel)
    {
        repetitions = parcel.readInt();
        weight = parcel.readFloat();
        exercise = parcel.readParcelable(Exercise.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(repetitions);
        dest.writeFloat(weight);
        dest.writeParcelable(exercise, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public ExerciseSet createFromParcel(Parcel in)
        {
            return new ExerciseSet(in);
        }

        public ExerciseSet[] newArray(int size)
        {
            return new ExerciseSet[size];
        }
    };

    public int getRepetitions()
    {
        return repetitions;
    }

    public void setRepetitions(int repetitions)
    {
        this.repetitions = repetitions;
    }

    public float getWeight()
    {
        return weight;
    }

    public void setWeight(float weight)
    {
        this.weight = weight;
    }
}
