export function fitByUnit(value, unit) {
    const units = ['B', 'KB', 'MB', 'GB', "TB"];
    let index = units.indexOf(unit);

    while ((value < 1 && value !== 0 || value > 1024) && (index >= 0 && index < units.length)) {
        if (value > 1024) {
            value = value / 1024;
            index++;
        } else {
            value = value * 1024;
            index--;
        }
    }

    // 添加类型检查
    if (typeof value === 'number') {
        return value.toFixed(2) + " " + units[index];
    } else {
        // 处理错误情况，可能是抛出错误或者返回一个错误信息
        console.error('Value is not a number:', value);
        return null; // 或者其他适当的错误处理
    }
}
export default {fitByUnit}