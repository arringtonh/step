// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    int minutesInOneDay = 24 * 60;
    if (request.getDuration() > minutesInOneDay)
        return new ArrayList<TimeRange>();

    Collection<TimeRange> withOptional = new ArrayList<>();
    Collection<TimeRange> mandatoryOnly = new ArrayList<>();
    withOptional.add(TimeRange.WHOLE_DAY);
    mandatoryOnly.add(TimeRange.WHOLE_DAY);

    Collection<String> allAttendees = new ArrayList<>();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());
    for (Event event : events) {
        if (!areAttendeesAvailable(allAttendees, event))
        {
            withOptional = availableTimes(withOptional, event, request.getDuration());
        }
        if (!areAttendeesAvailable(request.getAttendees(), event))
        {
            mandatoryOnly = availableTimes(mandatoryOnly, event, request.getDuration());
        }
    }

    if (request.getAttendees().isEmpty() || !withOptional.isEmpty())
        return withOptional;
    else {
        return mandatoryOnly;
    }
  }

  /**
    method returns true if all attendees will not show up to given event
  */
  public boolean areAttendeesAvailable(Collection<String> attendees, Event event) {
      for (String attendee : attendees) {
          if (event.getAttendees().contains(attendee))
            return false;
      }
      return true;
  }

  /**
    given the time available for a meeting and an event,
    if they overlap the method returns the time ranges that are still available

    ex:
    meeting:                  |-------|
    available times: |-------------|         |------------------------|
    result:          |--------|              |------------------------|
  */
  public Collection<TimeRange> availableTimes(Collection<TimeRange> availabilities, Event event, long requestDuration) {
      TimeRange eventTime = event.getWhen();
      Collection<TimeRange> ret = new ArrayList<>();
      for (TimeRange time : availabilities) {
          if (time.overlaps(eventTime)) {
              if (time.start() < eventTime.start() && eventTime.start() - time.start() >= requestDuration) // available time before event
              {
                  int duration = eventTime.start() - time.start();
                  ret.add(TimeRange.fromStartDuration(time.start(), duration));
              }
              if (eventTime.end() < time.end() && time.end() - eventTime.end() >= requestDuration) // available time after event
              {
                  int duration = time.end() - eventTime.end();
                  ret.add(TimeRange.fromStartDuration(eventTime.end(), duration));
              }
          } else {
              ret.add(time);
          }
      }
      return ret;
  }

}
