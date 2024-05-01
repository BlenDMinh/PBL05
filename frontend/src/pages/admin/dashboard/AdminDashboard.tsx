import { FaGamepad, FaUser } from "react-icons/fa";
import { blankAvatar } from "src/assets/images";
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';

export default function AdminDashboard() {
    const data = [
        {
          name: 'Page A',
          uv: 4000,
          pv: 2400,
          amt: 2400,
        },
        {
          name: 'Page B',
          uv: 3000,
          pv: 1398,
          amt: 2210,
        },
        {
          name: 'Page C',
          uv: 2000,
          pv: 9800,
          amt: 2290,
        },
        {
          name: 'Page D',
          uv: 2780,
          pv: 3908,
          amt: 2000,
        },
        {
          name: 'Page E',
          uv: 1890,
          pv: 4800,
          amt: 2181,
        },
        {
          name: 'Page F',
          uv: 2390,
          pv: 3800,
          amt: 2500,
        },
        {
          name: 'Page G',
          uv: 3490,
          pv: 4300,
          amt: 2100,
        },
      ];
    return (
        <div className="w-full h-full flex flex-col px-10 gap-10">
            <div className="flex w-full h-1/2 min-h-[50%] gap-10">
                <div className="h-full grow bg-gradient-to-b rounded-xl from-secondary to-transparent p-px">
                    <div className="h-full w-full rounded-xl bg-base-200 p-5">
                        <ResponsiveContainer width="100%" height="100%">
                            <AreaChart data={data} >
                                <defs>
                                    <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
                                        <stop offset="5%" stopColor="#8884d8" stopOpacity={0.8}/>
                                        <stop offset="70%" stopColor="#8884d8" stopOpacity={0}/>
                                    </linearGradient>
                                    <linearGradient id="colorPv" x1="0" y1="0" x2="0" y2="1">
                                        <stop offset="5%" stopColor="#82ca9d" stopOpacity={0.8}/>
                                        <stop offset="70%" stopColor="#82ca9d" stopOpacity={0}/>
                                    </linearGradient>
                                </defs>
                                <Tooltip />
                                <XAxis dataKey="name" axisLine={false} tickLine={false} />
                                <YAxis axisLine={false} tickLine={false} />
                                <Area type="monotone" dataKey="uv" stroke="#8884d8" fillOpacity={1} fill="url(#colorUv)" />
                                <Area type="monotone" dataKey="pv" stroke="#82ca9d" fillOpacity={1} fill="url(#colorPv)" />
                            </AreaChart>
                        </ResponsiveContainer>
                    </div>
                </div>
                <div className="h-full w-96 flex flex-col gap-10">
                    <div className="grow bg-gradient-to-b rounded-xl from-secondary to-transparent p-px">
                        <div className="h-full w-full rounded-xl bg-base-200 stat">
                            <div className="stat-figure text-info text-4xl">
                                <FaUser />
                            </div>
                            <div className="stat-title">Number of Players</div>
                            <div className="stat-value text-info">1000</div>
                            <div className="stat-desc">21% more than last month</div>
                        </div>
                    </div>
                    <div className="grow bg-gradient-to-b rounded-xl from-secondary to-transparent p-px">
                        <div className="h-full w-full rounded-xl bg-base-200 stat">
                            <div className="stat-figure text-primary text-4xl">
                                <FaGamepad />
                            </div>
                            <div className="stat-title">Number of Games</div>
                            <div className="stat-value text-primary">89,400</div>
                            <div className="stat-desc">21% more than last month</div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="bg-gradient-to-b rounded-xl from-secondary to-transparent p-px">
                <div className="h-full w-full rounded-xl bg-base-200 p-5">
                    <span className="text-xl">Top Ranking</span>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Name</th>
                                <th>Elo</th>
                                <th>Games</th>
                                <th>Win rate (%)</th>
                            </tr>
                        </thead>
                        {Array.from({ length: 10 }).map((_, index) => (
                        <tbody>
                            <tr>
                                <th>{index + 1}</th>
                                <td className="flex items-center gap-5">
                                    <div className="avatar">
                                        <div className="w-8 h-8 rounded-full">
                                            <img src={blankAvatar} alt="" />
                                        </div>
                                    </div>
                                    <span>Hart Hagerty</span>
                                </td>
                                <td>2500</td>
                                <td>635</td>
                                <td>80.9</td>
                            </tr>
                        </tbody> ))}
                    </table>
                </div>
            </div>
        </div>
    )
}