package kromm.com.workoutnotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Steffen on 17.06.2016.
 */
public class Workout implements Parcelable
{
    String name;
    Date date;
    private List<Exercise> exercises = new ArrayList<>();
    private List<WorkoutChangeListener> listeners = new ArrayList<>();

    protected Workout(Parcel in)
    {
        name = in.readString();
        exercises = in.createTypedArrayList(Exercise.CREATOR);
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>()
    {
        @Override
        public Workout createFromParcel(Parcel in)
        {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size)
        {
            return new Workout[size];
        }
    };

    public int numberOfExercises()
    {
        return exercises.size();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeTypedList(exercises);
    }

    public int getIndex(Exercise exercise)
    {
        return exercises.indexOf(exercise);
    }

    public interface WorkoutChangeListener
    {
        void onChange(Workout workout, Exercise exercise, boolean removed);
    }

    public Workout(String name)
    {
        this.name = name;
        date = new Date(System.currentTimeMillis());
    }

    public void removeListener(WorkoutChangeListener listener)
    {
        listeners.remove(listener);
    }

    public void addListener(WorkoutChangeListener listener)
    {
        listeners.add(listener);
    }

    public String getDisplayName()
    {
        return "Chest Workout";
    }

    public Exercise getExercise(int i)
    {
        return exercises.get(i);
    }

    public void switchExercise(Exercise exercise1, Exercise exercise2)
    {
        exercises.set(exercises.indexOf(exercise1), exercise2);
        exercises.set(exercises.indexOf(exercise2), exercise1);
        for (WorkoutChangeListener listener : listeners) {
            listener.onChange(this, null, false);
        }
    }

    public void removeExercise(Exercise exercise)
    {
        exercises.remove(exercise);
        for (WorkoutChangeListener listener : listeners) {
            listener.onChange(this, exercise, true);
        }
    }

    public void addExercise(Exercise exercise)
    {
        exercises.add(exercise);
        for (WorkoutChangeListener listener : listeners) {
            listener.onChange(this, exercise, false);
        }
    }
}
