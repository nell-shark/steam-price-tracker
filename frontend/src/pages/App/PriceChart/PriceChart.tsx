import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from "recharts";

export type Point = {
  name: string;
  RUB?: number;
  KZT?: number;
  USD?: number;
  EUR?: number;
};

export type PriceChartProps = {
  readonly data: Point[];
};

export function PriceChart({ data }: PriceChartProps) {
  return (
    <ResponsiveContainer
      width="100%"
      height="100%"
      minWidth="100%"
      minHeight={400}
      className="mt-4"
    >
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="name" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Line type="monotone" dataKey="RUB" stroke="#8884d8" />
        <Line type="monotone" dataKey="KZT" stroke="#82ca9d" />
        <Line type="monotone" dataKey="USD" stroke="#ffc658" />
        <Line type="monotone" dataKey="EUR" stroke="#607D8B" />
      </LineChart>
    </ResponsiveContainer>
  );
}
