#!/usr/bin/env python3
"""小脚印批量激活码生成器（卖家端，预留名额用）

把要发码的设备 ID 逐行填进 device_ids.txt（每行一个 8 位十六进制），然后运行：
  python batch_generate_licenses.py [device_ids.txt]

默认输出到 licenses_output.txt；可在命令行指定输出文件：
  python batch_generate_licenses.py device_ids.txt out.txt
"""

import sys
from pathlib import Path

from generate_license import generate


def main() -> None:
    input_file = sys.argv[1] if len(sys.argv) > 1 else "device_ids.txt"
    output_file = sys.argv[2] if len(sys.argv) > 2 else "licenses_output.txt"

    ids = [
        line.strip().upper()
        for line in Path(input_file).read_text(encoding="utf-8").splitlines()
        if line.strip() and not line.strip().startswith("#")
    ]
    if not ids:
        print("device_ids.txt 里没有设备 ID，请先填写")
        sys.exit(1)

    lines = ["# 小脚印永久激活码（绑定设备）", f"# 共 {len(ids)} 个，生成时间见文件名"]
    for device_id in ids:
        code = generate(device_id, "L", 0)
        lines.append(f"{device_id}  {code}")

    Path(output_file).write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"已生成 {len(ids)} 个永久激活码 -> {output_file}")
    for line in lines[2:]:
        print(line)


if __name__ == "__main__":
    main()
