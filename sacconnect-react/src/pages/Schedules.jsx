import { useMemo, useState } from 'react';
import { ArrowLeft, CalendarDays, Clock3, Star, School } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

function formatMinutes(totalMinutes) {
  const hours24 = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  const suffix = hours24 >= 12 ? 'PM' : 'AM';
  const hours12 = hours24 % 12 === 0 ? 12 : hours24 % 12;
  return `${hours12}:${String(minutes).padStart(2, '0')} ${suffix}`;
}

function formatMeetings(meetings = []) {
  return meetings
    .map(m => `${m.dayOfWeek} ${formatMinutes(m.startMin)}-${formatMinutes(m.endMin)}`)
    .join(' | ');
}

function computeScheduleMetrics(option) {
  const sections = option?.sections || [];
  const courseCount = sections.length;
  const cumulativeProfessorRating = Number(option?.totalProfessorRating || 0);
  const normalizedProfessorRating = courseCount > 0 ? cumulativeProfessorRating / courseCount : 0;

  const allMeetings = sections.flatMap(section =>
    (section.meetings || []).map(meeting => ({
      ...meeting,
      professorRating: Number(section.professorOverallRating || 0)
    }))
  );

  const byDay = allMeetings.reduce((acc, meeting) => {
    if (!acc[meeting.dayOfWeek]) acc[meeting.dayOfWeek] = [];
    acc[meeting.dayOfWeek].push(meeting);
    return acc;
  }, {});

  let hasGapOverTwoHours = false;
  let maxDailyCampusMinutes = 0;
  let earliestStart = Infinity;

  Object.values(byDay).forEach(dayMeetings => {
    dayMeetings.sort((a, b) => a.startMin - b.startMin);
    if (!dayMeetings.length) return;

    earliestStart = Math.min(earliestStart, dayMeetings[0].startMin);

    const firstStart = dayMeetings[0].startMin;
    const lastEnd = dayMeetings[dayMeetings.length - 1].endMin;
    maxDailyCampusMinutes = Math.max(maxDailyCampusMinutes, lastEnd - firstStart);

    for (let i = 1; i < dayMeetings.length; i += 1) {
      const gap = dayMeetings[i].startMin - dayMeetings[i - 1].endMin;
      if (gap > 120) {
        hasGapOverTwoHours = true;
        break;
      }
    }
  });

  const hasProfessorBelowTwoPointFive = sections.some(
    section => Number(section.professorOverallRating || 0) < 2.5
  );

  let scheduleScore = normalizedProfessorRating;

  if (hasGapOverTwoHours) scheduleScore -= 0.5;
  if (hasProfessorBelowTwoPointFive) scheduleScore -= 0.5;
  if (maxDailyCampusMinutes > 360) scheduleScore -= 1.0;
  if (earliestStart < 480) scheduleScore -= 1.0;
  else if (earliestStart < 540) scheduleScore -= 0.5;

  scheduleScore = Math.max(1, Math.min(5, scheduleScore));

  return {
    courseCount,
    cumulativeProfessorRating,
    cumulativeMax: courseCount * 5,
    normalizedProfessorRating,
    scheduleScore
  };
}

export default function Schedules() {
  const location = useLocation();
  const scheduleResult = location.state?.scheduleResult;
  const selectedClasses = location.state?.selectedClasses || [];
  const [activeTab, setActiveTab] = useState('overallBest');

  const tabs = useMemo(() => ([
    { key: 'overallBest', label: 'Overall Best' },
    { key: 'leastDays', label: 'Least Days' },
    { key: 'bestTime', label: 'Best Time' },
    { key: 'smallestGaps', label: 'Smallest Gaps' }
  ]), []);

  if (!scheduleResult) {
    return (
      <div className="min-h-screen bg-[#004e38] text-white p-8">
        <Link to="/majors" className="inline-flex items-center gap-2 text-ss-gold font-bold">
          <ArrowLeft size={16} /> Back to Majors
        </Link>
        <div className="max-w-2xl mt-10 bg-ss-card border border-white/10 rounded-3xl p-8">
          <h1 className="text-3xl font-black uppercase tracking-tight">No Schedule Data</h1>
          <p className="mt-3 text-white/70">Generate schedules from the Majors page first.</p>
        </div>
      </div>
    );
  }

  const activeSchedules = scheduleResult[activeTab] || [];

  return (
    <div className="min-h-screen bg-[#004e38] text-white p-6 md:p-12">
      <Link to="/majors" className="fixed top-10 left-10 text-white/60 hover:text-ss-gold transition z-50">
        <ArrowLeft size={28} />
      </Link>

      <header className="max-w-7xl mx-auto pt-12 mb-10">
        <h1 className="text-5xl md:text-6xl font-black uppercase italic tracking-tighter">
          Generated <span className="text-ss-gold underline decoration-white underline-offset-8">Schedules</span>
        </h1>
        <p className="mt-4 text-white/75 font-medium">
          Courses selected: {selectedClasses.join(', ')} | Valid combinations: {scheduleResult.totalValidSchedules}
          {scheduleResult.truncated ? ' (truncated)' : ''}
        </p>
      </header>

      <main className="max-w-7xl mx-auto">
        <div className="flex flex-wrap gap-3 mb-8">
          {tabs.map(tab => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`px-5 py-3 rounded-xl border font-bold text-xs uppercase tracking-wider transition ${
                activeTab === tab.key
                  ? 'bg-ss-gold text-black border-ss-gold'
                  : 'bg-ss-card border-white/20 text-white/80 hover:border-ss-gold/60'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {!activeSchedules.length ? (
          <div className="bg-ss-card border border-white/10 rounded-3xl p-8 text-white/70">
            No schedules available for this category.
          </div>
        ) : (
          <div className="space-y-6">
            {activeSchedules.map((option, idx) => (
              <section key={`${activeTab}-${idx}`} className="bg-ss-card border border-white/10 rounded-3xl p-7 shadow-2xl">
                {(() => {
                  const metrics = computeScheduleMetrics(option);
                  return (
                    <>
                <div className="flex flex-wrap items-center gap-4 mb-5">
                  <h2 className="text-2xl font-black uppercase tracking-tight">Schedule #{idx + 1}</h2>
                  <span className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-white/80 bg-white/5 border border-white/20 rounded-full px-3 py-1">
                    <CalendarDays size={14} /> Days: {option.distinctDays}
                  </span>
                  <span className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-white/80 bg-white/5 border border-white/20 rounded-full px-3 py-1">
                    <Clock3 size={14} /> Gaps: {option.totalGapMinutes}m
                  </span>
                  <span className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-white/80 bg-white/5 border border-white/20 rounded-full px-3 py-1">
                    <Clock3 size={14} /> Outside 9-5: {option.outsidePreferredMinutes}m
                  </span>
                  <span className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-black bg-ss-gold rounded-full px-3 py-1">
                    <Star size={14} /> Cumulative Rating: {metrics.cumulativeProfessorRating.toFixed(1)} / {metrics.cumulativeMax}
                  </span>
                  <span className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-black bg-ss-gold rounded-full px-3 py-1">
                    <Star size={14} /> Schedule Score: {metrics.scheduleScore.toFixed(1)} / 5
                  </span>
                </div>
                <p className="text-xs text-white/70 mb-5">
                  Professor average: {metrics.normalizedProfessorRating.toFixed(1)} / 5
                </p>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                  {option.sections?.map(section => (
                    <article key={section.sectionId} className="border border-white/10 rounded-2xl p-4 bg-white/5">
                      <p className="text-sm font-black text-ss-gold uppercase tracking-wide">
                        {section.courseCode} - {section.courseTitle}
                      </p>
                      <p className="text-sm mt-1">
                        Section {section.sectionNumber} (CRN: {section.sourceCrn})
                      </p>
                      <p className="text-sm mt-2 flex items-center gap-2 text-white/85">
                        <School size={14} /> {section.professorName}
                        <span className="text-ss-gold font-bold">
                          ({section.professorOverallRating} / {section.professorReviewCount} reviews)
                        </span>
                      </p>
                      <p className="text-xs text-white/75 mt-2">
                        {formatMeetings(section.meetings)}
                      </p>
                    </article>
                  ))}
                </div>
                    </>
                  );
                })()}
              </section>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
